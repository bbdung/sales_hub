package com.apus.salehub.adapter.out.agent;

import com.apus.salehub.adapter.out.agent.model.AgentConfig;
import com.apus.salehub.adapter.out.agent.model.AgentResult;
import com.apus.salehub.adapter.out.agent.model.RawListing;
import com.apus.salehub.application.port.out.persistence.ProjectRepository;
import com.apus.salehub.application.port.out.persistence.ScrapeRunRepository;
import com.apus.salehub.application.service.DedupService;
import com.apus.salehub.application.service.ScoringService;
import com.apus.salehub.domain.model.Project;
import com.apus.salehub.domain.model.ScrapeRun;
import com.apus.salehub.domain.model.Source;
import com.apus.salehub.domain.model.enums.BudgetType;
import com.apus.salehub.domain.model.enums.ScrapeRunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public abstract class BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(BaseAgent.class);

    public abstract String getSourceName();
    public abstract String getDisplayName();
    public abstract List<RawListing> fetchListings(AgentConfig config);

    protected Project normalize(RawListing raw) {
        Project project = new Project();
        project.setExternalId(raw.getExternalId());
        project.setTitle(raw.getTitle());
        project.setDescription(raw.getDescription());
        project.setUrl(raw.getUrl());
        if (raw.getBudgetMin() != null) project.setBudgetMin(BigDecimal.valueOf(raw.getBudgetMin()));
        if (raw.getBudgetMax() != null) project.setBudgetMax(BigDecimal.valueOf(raw.getBudgetMax()));
        project.setBudgetCurrency(raw.getBudgetCurrency());
        if (raw.getBudgetType() != null) {
            project.setBudgetType(BudgetType.fromValue(raw.getBudgetType()));
        }
        project.setTimeline(raw.getTimeline());
        project.setPostedAt(raw.getPostedAt());
        project.setRawData(raw.getRawData());
        project.setFingerprint(
            ScoringService.computeFingerprint(raw.getTitle(), raw.getDescription(), getSourceName())
        );
        return project;
    }

    public AgentResult run(ProjectRepository projectRepo, ScrapeRunRepository scrapeRunRepo,
                           DedupService dedupService, ScoringService scoringService,
                           Source source) {
        AgentConfig config = new AgentConfig();
        if (source.getConfigJson() != null) {
            config.setExtra(source.getConfigJson());
        }

        AgentResult result = new AgentResult(getSourceName());

        ScrapeRun scrapeRun = new ScrapeRun();
        scrapeRun.setSource(source);
        scrapeRun.setStatus(ScrapeRunStatus.RUNNING);
        scrapeRun.setStartedAt(OffsetDateTime.now());
        scrapeRunRepo.save(scrapeRun);

        try {
            log.info("[{}] Fetching listings...", getSourceName());
            List<RawListing> listings = fetchListings(config);
            result.setTotalFound(listings.size());
            log.info("[{}] Found {} listings", getSourceName(), listings.size());

            for (RawListing raw : listings) {
                try {
                    Optional<Long> existingId = dedupService.checkDuplicate(
                        raw.getTitle(), raw.getDescription(), getSourceName());
                    if (existingId.isPresent()) {
                        result.setDuplicates(result.getDuplicates() + 1);
                        continue;
                    }

                    Project project = normalize(raw);
                    project.setSource(source);
                    project = projectRepo.save(project);

                    ScoringService.ScoreResult scoreResult = scoringService.scoreProject(project);
                    project.setScore(scoreResult.score());
                    project.setScoreBreakdown(scoreResult.breakdown());
                    projectRepo.save(project);

                    result.setNewProjects(result.getNewProjects() + 1);
                } catch (Exception e) {
                    log.error("[{}] Error processing listing: {}", getSourceName(), e.getMessage());
                    result.getErrors().add(e.getMessage());
                }
            }

            scrapeRun.setStatus(ScrapeRunStatus.SUCCESS);
            scrapeRun.setFinishedAt(OffsetDateTime.now());
            scrapeRun.setProjectsFound(result.getTotalFound());
            scrapeRun.setProjectsNew(result.getNewProjects());
            scrapeRunRepo.save(scrapeRun);

        } catch (Exception e) {
            log.error("[{}] Agent run failed: {}", getSourceName(), e.getMessage());
            scrapeRun.setStatus(ScrapeRunStatus.FAILED);
            scrapeRun.setFinishedAt(OffsetDateTime.now());
            scrapeRun.setErrorMessage(e.getMessage());
            scrapeRunRepo.save(scrapeRun);
            result.getErrors().add(e.getMessage());
        }

        return result;
    }
}

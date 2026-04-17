package com.apus.salehub.adapter.out.agent;

import com.apus.salehub.adapter.out.agent.model.AgentResult;
import com.apus.salehub.adapter.out.agent.source.*;
import com.apus.salehub.application.port.out.persistence.ProjectRepository;
import com.apus.salehub.application.port.out.persistence.ScrapeRunRepository;
import com.apus.salehub.application.port.out.persistence.SourceRepository;
import com.apus.salehub.application.service.DedupService;
import com.apus.salehub.application.service.ScoringService;
import com.apus.salehub.domain.model.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class AgentOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AgentOrchestrator.class);

    private static final Map<String, Supplier<BaseAgent>> AGENT_REGISTRY = new HashMap<>();

    static {
        AGENT_REGISTRY.put("upwork", UpworkAgent::new);
        AGENT_REGISTRY.put("github", GitHubAgent::new);
        AGENT_REGISTRY.put("freelancer", FreelancerAgent::new);
        //AGENT_REGISTRY.put("linkedin", LinkedInAgent::new);
        //AGENT_REGISTRY.put("twitter", TwitterAgent::new);
        AGENT_REGISTRY.put("reddit", RedditAgent::new);
    }

    private final SourceRepository sourceRepository;
    private final ProjectRepository projectRepository;
    private final ScrapeRunRepository scrapeRunRepository;
    private final DedupService dedupService;
    private final ScoringService scoringService;

    public AgentOrchestrator(SourceRepository sourceRepository,
                              ProjectRepository projectRepository,
                              ScrapeRunRepository scrapeRunRepository,
                              DedupService dedupService,
                              ScoringService scoringService) {
        this.sourceRepository = sourceRepository;
        this.projectRepository = projectRepository;
        this.scrapeRunRepository = scrapeRunRepository;
        this.dedupService = dedupService;
        this.scoringService = scoringService;
    }

    @Transactional
    public List<AgentResult> runAgents(List<String> sourceNames, List<String> keywords) {
        List<AgentResult> results = new ArrayList<>();

        List<Source> sources;
        if (sourceNames != null && !sourceNames.isEmpty()) {
            sources = sourceRepository.findByNameIn(sourceNames);
        } else {
            sources = sourceRepository.findByIsActiveTrue();
        }

        for (Source source : sources) {
            Supplier<BaseAgent> agentFactory = AGENT_REGISTRY.get(source.getName());
            if (agentFactory == null) {
                log.warn("No agent registered for source: {}", source.getName());
                continue;
            }

            BaseAgent agent = agentFactory.get();
            log.info("Running agent: {}", source.getName());

            AgentResult result = agent.run(
                projectRepository, scrapeRunRepository,
                dedupService, scoringService, source
            );
            results.add(result);

            log.info("Agent {} done: {} new, {} dupes",
                source.getName(), result.getNewProjects(), result.getDuplicates());
        }

        return results;
    }
}

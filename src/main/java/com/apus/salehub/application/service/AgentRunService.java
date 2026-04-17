package com.apus.salehub.application.service;

import com.apus.salehub.adapter.out.agent.AgentOrchestrator;
import com.apus.salehub.adapter.out.agent.model.AgentResult;
import com.apus.salehub.application.port.in.agent.AgentRunUseCase;
import com.apus.salehub.application.port.out.persistence.ScrapeRunRepository;
import com.apus.salehub.domain.model.ScrapeRun;
import com.apus.salehub.domain.model.enums.ScrapeRunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgentRunService implements AgentRunUseCase {

    private static final Logger log = LoggerFactory.getLogger(AgentRunService.class);

    private final AgentOrchestrator orchestrator;
    private final ScrapeRunRepository scrapeRunRepository;

    public AgentRunService(AgentOrchestrator orchestrator, ScrapeRunRepository scrapeRunRepository) {
        this.orchestrator = orchestrator;
        this.scrapeRunRepository = scrapeRunRepository;
    }

    @Async("agentExecutor")
    public void runAgentsAsync(List<String> sourceNames, List<String> keywords) {
        try {
            List<AgentResult> results = orchestrator.runAgents(sourceNames, keywords);
            for (AgentResult r : results) {
                log.info("[{}] Found: {}, New: {}, Dupes: {}",
                    r.getSourceName(), r.getTotalFound(), r.getNewProjects(), r.getDuplicates());
            }
        } catch (Exception e) {
            log.error("Agent run failed: {}", e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<ScrapeRun> getRunningAgents() {
        return scrapeRunRepository.findByStatus(ScrapeRunStatus.RUNNING);
    }

    @Transactional(readOnly = true)
    public List<ScrapeRun> getHistory(int limit) {
        return scrapeRunRepository.findAllByOrderByStartedAtDesc(PageRequest.of(0, limit));
    }
}
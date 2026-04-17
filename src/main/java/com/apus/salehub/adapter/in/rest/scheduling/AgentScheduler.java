package com.apus.salehub.adapter.in.rest.scheduling;

import com.apus.salehub.application.service.AgentRunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AgentScheduler {

    private static final Logger log = LoggerFactory.getLogger(AgentScheduler.class);

    private final AgentRunService agentRunService;

    public AgentScheduler(AgentRunService agentRunService) {
        this.agentRunService = agentRunService;
    }

    @Scheduled(fixedDelayString = "#{${apus.scrape-interval-minutes:60} * 60000}",
               initialDelayString = "60000")
    public void runAllAgents() {
        log.info("Scheduled agent run starting...");
        agentRunService.runAgentsAsync(null, null);
    }
}

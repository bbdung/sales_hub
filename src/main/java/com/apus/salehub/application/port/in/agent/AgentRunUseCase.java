package com.apus.salehub.application.port.in.agent;

import com.apus.salehub.domain.model.ScrapeRun;

import java.util.List;

public interface AgentRunUseCase {

    void runAgentsAsync(List<String> sourceNames, List<String> keywords);

    List<ScrapeRun> getRunningAgents();

    List<ScrapeRun> getHistory(int limit);
}
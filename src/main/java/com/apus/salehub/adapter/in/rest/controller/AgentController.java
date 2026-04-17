package com.apus.salehub.adapter.in.rest.controller;

import com.apus.base.response.ResponseWrapper;
import com.apus.salehub.adapter.in.rest.dto.agent.AgentRunRequestDto;
import com.apus.salehub.adapter.in.rest.dto.agent.AgentRunResponseDto;
import com.apus.salehub.adapter.in.rest.dto.agent.ScrapeRunOutDto;
import com.apus.salehub.application.port.in.agent.AgentRunUseCase;
import com.apus.salehub.domain.model.ScrapeRun;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agents")
@Tag(name = "Agent", description = "Agent scraping operations")
@ResponseWrapper
public class AgentController {

    private final AgentRunUseCase agentRunUseCase;

    public AgentController(AgentRunUseCase agentRunUseCase) {
        this.agentRunUseCase = agentRunUseCase;
    }

    @PostMapping("/run")
    public AgentRunResponseDto triggerRun(@RequestBody AgentRunRequestDto request) {
        List<String> sources = request.getSources();
        String sourceLabel = sources.isEmpty() ? "all active" : String.join(", ", sources);

        agentRunUseCase.runAgentsAsync(
            sources.isEmpty() ? null : sources,
            request.getKeywords()
        );

        return new AgentRunResponseDto("Agent run started for: " + sourceLabel);
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        List<ScrapeRun> running = agentRunUseCase.getRunningAgents();
        List<Map<String, Object>> tasks = running.stream()
            .map(r -> Map.<String, Object>of(
                "id", r.getId(),
                "source_id", r.getSourceId(),
                "started_at", r.getStartedAt().toString()
            ))
            .toList();
        return Map.of("running", running.size(), "tasks", tasks);
    }

    @GetMapping("/history")
    public List<ScrapeRunOutDto> getHistory(@RequestParam(defaultValue = "50") int limit) {
        List<ScrapeRun> runs = agentRunUseCase.getHistory(limit);
        return runs.stream()
            .map(r -> new ScrapeRunOutDto(
                r.getId(),
                r.getSource() != null ? r.getSource().getDisplayName() : String.valueOf(r.getSourceId()),
                r.getStatus().getValue(),
                r.getProjectsFound(),
                r.getProjectsNew(),
                r.getErrorMessage()
            ))
            .toList();
    }
}
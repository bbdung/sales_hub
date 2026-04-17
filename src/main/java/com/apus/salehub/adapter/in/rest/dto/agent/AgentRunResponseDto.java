package com.apus.salehub.adapter.in.rest.dto.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record AgentRunResponseDto(String message, List<Map<String, Object>> results) {

    public AgentRunResponseDto(String message) {
        this(message, new ArrayList<>());
    }
}

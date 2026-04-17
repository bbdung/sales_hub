package com.apus.salehub.adapter.in.rest.dto.agent;

public record ScrapeRunOutDto(
    Long id,
    String sourceName,
    String status,
    int projectsFound,
    int projectsNew,
    String errorMessage
) {}

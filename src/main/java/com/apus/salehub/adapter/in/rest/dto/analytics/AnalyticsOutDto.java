package com.apus.salehub.adapter.in.rest.dto.analytics;

import java.util.List;

public record AnalyticsOutDto(
    SummaryStats summary,
    List<SourceStats> bySource,
    List<SkillStats> topSkills,
    List<TrendPoint> trend
) {

    public record SummaryStats(
        int totalProjects,
        int newProjects,
        int contacted,
        int applied,
        int won,
        int lost,
        double avgScore,
        String topSource
    ) {}

    public record SourceStats(String sourceName, int total, int newCount, double avgScore) {}

    public record SkillStats(String skillName, int count) {}

    public record TrendPoint(String date, int count) {}
}

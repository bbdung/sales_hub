package com.apus.salehub.application.port.in.analytics;

import java.util.List;

public interface AnalyticsUseCase {

    record SummaryStats(
        int totalProjects, int newProjects, int contacted, int applied,
        int won, int lost, double avgScore, String topSource
    ) {}

    record SourceStats(String sourceName, int total, int newCount, double avgScore) {}

    record SkillStats(String skillName, int count) {}

    record TrendPoint(String date, int count) {}

    record AnalyticsSummary(
        SummaryStats summary, List<SourceStats> bySource,
        List<SkillStats> topSkills, List<TrendPoint> trend
    ) {}

    AnalyticsSummary getSummary();
}
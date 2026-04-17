package com.apus.salehub.adapter.in.rest.controller;

import com.apus.base.response.ResponseWrapper;
import com.apus.salehub.adapter.in.rest.dto.analytics.AnalyticsOutDto;
import com.apus.salehub.application.port.in.analytics.AnalyticsUseCase;
import com.apus.salehub.application.port.in.analytics.AnalyticsUseCase.AnalyticsSummary;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Analytics and reporting operations")
@ResponseWrapper
public class AnalyticsController {

    private final AnalyticsUseCase analyticsUseCase;

    public AnalyticsController(AnalyticsUseCase analyticsUseCase) {
        this.analyticsUseCase = analyticsUseCase;
    }

    @GetMapping("/summary")
    public AnalyticsOutDto getSummary() {
        AnalyticsSummary data = analyticsUseCase.getSummary();
        return new AnalyticsOutDto(
            new AnalyticsOutDto.SummaryStats(
                data.summary().totalProjects(), data.summary().newProjects(),
                data.summary().contacted(), data.summary().applied(),
                data.summary().won(), data.summary().lost(),
                data.summary().avgScore(), data.summary().topSource()
            ),
            data.bySource().stream()
                .map(s -> new AnalyticsOutDto.SourceStats(s.sourceName(), s.total(), s.newCount(), s.avgScore()))
                .toList(),
            data.topSkills().stream()
                .map(s -> new AnalyticsOutDto.SkillStats(s.skillName(), s.count()))
                .toList(),
            data.trend().stream()
                .map(t -> new AnalyticsOutDto.TrendPoint(t.date(), t.count()))
                .toList()
        );
    }
}
package com.apus.salehub.application.service;

import com.apus.salehub.application.port.in.analytics.AnalyticsUseCase;
import com.apus.salehub.application.port.out.persistence.ProjectRepository;
import com.apus.salehub.domain.model.enums.ProjectStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsService implements AnalyticsUseCase {

    private final ProjectRepository projectRepository;

    @PersistenceContext
    private EntityManager em;

    public AnalyticsService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public AnalyticsSummary getSummary() {
        int total = (int) projectRepository.count();
        int newCount = (int) projectRepository.countByStatus(ProjectStatus.NEW);
        int contacted = (int) projectRepository.countByStatus(ProjectStatus.CONTACTED);
        int applied = (int) projectRepository.countByStatus(ProjectStatus.APPLIED);
        int won = (int) projectRepository.countByStatus(ProjectStatus.WON);
        int lost = (int) projectRepository.countByStatus(ProjectStatus.LOST);
        Double avgScore = projectRepository.findAverageScore();
        if (avgScore == null) avgScore = 0.0;

        @SuppressWarnings("unchecked")
        List<Object[]> sourceRows = em.createQuery(
            "SELECT s.displayName, COUNT(p), AVG(p.score) " +
            "FROM Project p JOIN p.source s GROUP BY s.displayName"
        ).getResultList();

        List<SourceStats> bySource = new ArrayList<>();
        for (Object[] row : sourceRows) {
            bySource.add(new SourceStats(
                (String) row[0],
                ((Number) row[1]).intValue(),
                0,
                row[2] != null ? ((Number) row[2]).doubleValue() : 0.0
            ));
        }

        String topSource = bySource.isEmpty() ? null : bySource.get(0).sourceName();

        @SuppressWarnings("unchecked")
        List<Object[]> skillRows = em.createNativeQuery(
            "SELECT sk.name, COUNT(ps.project_id) as cnt " +
            "FROM skills sk JOIN project_skills ps ON sk.id = ps.skill_id " +
            "GROUP BY sk.name ORDER BY cnt DESC LIMIT 20"
        ).getResultList();

        List<SkillStats> topSkills = new ArrayList<>();
        for (Object[] row : skillRows) {
            topSkills.add(new SkillStats(
                (String) row[0],
                ((Number) row[1]).intValue()
            ));
        }

        @SuppressWarnings("unchecked")
        List<Object[]> trendRows = em.createNativeQuery(
            "SELECT CAST(created_at AS DATE) as d, COUNT(*) " +
            "FROM projects GROUP BY d ORDER BY d LIMIT 30"
        ).getResultList();

        List<TrendPoint> trend = new ArrayList<>();
        for (Object[] row : trendRows) {
            trend.add(new TrendPoint(row[0].toString(), ((Number) row[1]).intValue()));
        }

        SummaryStats summary = new SummaryStats(
            total, newCount, contacted, applied, won, lost, avgScore, topSource
        );

        return new AnalyticsSummary(summary, bySource, topSkills, trend);
    }
}
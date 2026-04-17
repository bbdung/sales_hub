package com.apus.salehub.application.service;

import com.apus.salehub.config.AppProperties;
import com.apus.salehub.domain.model.Project;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScoringService {

    private static final Map<String, Double> WEIGHTS = Map.of(
        "skill_fit", 0.30,
        "budget", 0.25,
        "client_quality", 0.15,
        "clarity", 0.15,
        "recency", 0.10,
        "competition", 0.05
    );

    private final AppProperties properties;

    public ScoringService(AppProperties properties) {
        this.properties = properties;
    }

    public record ScoreResult(BigDecimal score, Map<String, Object> breakdown) {}

    public ScoreResult scoreProject(Project project) {
        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("skill_fit", scoreSkillFit(project));
        breakdown.put("budget", scoreBudget(project));
        breakdown.put("client_quality", scoreClientQuality(project));
        breakdown.put("clarity", scoreClarity(project));
        breakdown.put("recency", scoreRecency(project));
        breakdown.put("competition", scoreCompetition(project));

        double total = 0.0;
        for (var entry : breakdown.entrySet()) {
            double weight = WEIGHTS.getOrDefault(entry.getKey(), 0.0);
            total += ((Number) entry.getValue()).doubleValue() * weight;
        }

        BigDecimal score = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
        return new ScoreResult(score, breakdown);
    }

    private double scoreSkillFit(Project project) {
        if (project.getDescription() == null) return 50.0;
        String descLower = project.getDescription().toLowerCase();
        List<String> teamSkills = properties.getTeamSkills();
        if (teamSkills.isEmpty()) return 50.0;

        long matched = teamSkills.stream()
            .filter(skill -> descLower.contains(skill.toLowerCase()))
            .count();

        return Math.min(100.0, (matched / Math.max(teamSkills.size() * 0.3, 1)) * 100);
    }

    private double scoreBudget(Project project) {
        if (project.getBudgetMin() == null && project.getBudgetMax() == null) return 40.0;

        double budget = 0;
        if (project.getBudgetMax() != null) budget = project.getBudgetMax().doubleValue();
        else if (project.getBudgetMin() != null) budget = project.getBudgetMin().doubleValue();

        if (budget < 500) return 20.0;
        if (budget < 2000) return 50.0;
        if (budget < 10000) return 75.0;
        return 90.0;
    }

    private double scoreClientQuality(Project project) {
        Map<String, Object> raw = project.getRawData();
        if (raw == null) raw = Map.of();

        double score = 50.0;
        if (Boolean.TRUE.equals(raw.get("payment_verified"))) score += 20;

        Object hireRate = raw.get("hire_rate");
        if (hireRate instanceof Number && ((Number) hireRate).doubleValue() > 50) score += 15;

        Object totalSpent = raw.get("total_spent");
        if (totalSpent instanceof Number && ((Number) totalSpent).doubleValue() > 10000) score += 15;

        return Math.min(100.0, score);
    }

    private double scoreClarity(Project project) {
        if (project.getDescription() == null) return 20.0;
        int length = project.getDescription().length();
        if (length < 100) return 30.0;
        if (length < 500) return 60.0;
        if (length < 2000) return 80.0;
        return 90.0;
    }

    private double scoreRecency(Project project) {
        if (project.getPostedAt() == null) return 50.0;
        double ageHours = Duration.between(project.getPostedAt(), OffsetDateTime.now()).toSeconds() / 3600.0;
        if (ageHours < 6) return 100.0;
        if (ageHours < 24) return 85.0;
        if (ageHours < 72) return 65.0;
        if (ageHours < 168) return 40.0;
        return 20.0;
    }

    private double scoreCompetition(Project project) {
        Map<String, Object> raw = project.getRawData();
        if (raw == null) raw = Map.of();

        Object proposals = raw.get("proposals_count");
        int count = (proposals instanceof Number) ? ((Number) proposals).intValue() : 0;

        if (count == 0) return 90.0;
        if (count < 5) return 75.0;
        if (count < 15) return 50.0;
        if (count < 30) return 30.0;
        return 15.0;
    }

    public static String computeFingerprint(String title, String description, String source) {
        String content = (title != null ? title.toLowerCase().trim() : "") + "|"
            + (description != null ? description.substring(0, Math.min(200, description.length())).toLowerCase().trim() : "")
            + "|" + source;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                hex.append(String.format("%02x", hash[i]));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}

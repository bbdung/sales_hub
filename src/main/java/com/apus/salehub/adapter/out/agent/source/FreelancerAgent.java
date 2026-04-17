package com.apus.salehub.adapter.out.agent.source;

import com.apus.salehub.adapter.out.agent.BaseAgent;
import com.apus.salehub.adapter.out.agent.RateLimiter;
import com.apus.salehub.adapter.out.agent.model.AgentConfig;
import com.apus.salehub.adapter.out.agent.model.RawListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreelancerAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(FreelancerAgent.class);
    private final RateLimiter limiter = new RateLimiter(5);
    private final WebClient webClient = WebClient.create();

    @Override
    public String getSourceName() { return "freelancer"; }

    @Override
    public String getDisplayName() { return "Freelancer"; }

    @Override
    @SuppressWarnings("unchecked")
    public List<RawListing> fetchListings(AgentConfig config) {
        List<RawListing> listings = new ArrayList<>();
        List<String> keywords = config.getKeywords().isEmpty()
            ? List.of("python", "react", "mobile app")
            : config.getKeywords();

        for (String keyword : keywords.subList(0, Math.min(keywords.size(), config.getMaxPages()))) {
            try {
                limiter.acquire();

                Map<String, Object> data = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .scheme("https").host("www.freelancer.com")
                        .path("/api/projects/0.1/projects/active")
                        .queryParam("query", keyword)
                        .queryParam("compact", "true")
                        .queryParam("limit", 50)
                        .queryParam("sort_field", "time_submitted")
                        .queryParam("sort_direction", "desc")
                        .build())
                    .retrieve().bodyToMono(Map.class).block();

                if (data == null) continue;
                Map<String, Object> result = (Map<String, Object>) data.getOrDefault("result", Map.of());
                List<Map<String, Object>> projects = (List<Map<String, Object>>) result.getOrDefault("projects", List.of());

                for (Map<String, Object> project : projects) {
                    RawListing listing = parseProject(project);
                    if (listing != null) listings.add(listing);
                }
            } catch (Exception e) {
                log.error("Error fetching Freelancer projects: {}", e.getMessage());
            }
        }

        return listings;
    }

    @SuppressWarnings("unchecked")
    private RawListing parseProject(Map<String, Object> item) {
        try {
            String title = (String) item.getOrDefault("title", "");
            if (title.isBlank()) return null;

            Map<String, Object> budget = (Map<String, Object>) item.getOrDefault("budget", Map.of());
            Map<String, Object> currency = (Map<String, Object>) item.getOrDefault("currency", Map.of());

            RawListing listing = new RawListing();
            listing.setExternalId(String.valueOf(item.getOrDefault("id", "")));
            listing.setTitle(title.trim());
            listing.setDescription((String) item.getOrDefault("preview_description", ""));
            listing.setUrl("https://www.freelancer.com/projects/" + item.getOrDefault("seo_url", ""));

            Object minBudget = budget.get("minimum");
            Object maxBudget = budget.get("maximum");
            if (minBudget instanceof Number) listing.setBudgetMin(((Number) minBudget).doubleValue());
            if (maxBudget instanceof Number) listing.setBudgetMax(((Number) maxBudget).doubleValue());
            listing.setBudgetCurrency((String) currency.getOrDefault("code", "USD"));
            listing.setBudgetType((String) item.getOrDefault("type", "fixed"));

            Map<String, Object> bidStats = (Map<String, Object>) item.getOrDefault("bid_stats", Map.of());
            List<Map<String, Object>> jobs = (List<Map<String, Object>>) item.getOrDefault("jobs", List.of());

            Map<String, Object> rawData = new HashMap<>();
            rawData.put("bid_count", bidStats.getOrDefault("bid_count", 0));
            rawData.put("avg_bid", bidStats.getOrDefault("bid_avg", null));
            rawData.put("jobs", jobs.stream().map(j -> j.getOrDefault("name", "")).toList());
            listing.setRawData(rawData);

            return listing;
        } catch (Exception e) {
            log.error("Error parsing Freelancer project: {}", e.getMessage());
            return null;
        }
    }
}

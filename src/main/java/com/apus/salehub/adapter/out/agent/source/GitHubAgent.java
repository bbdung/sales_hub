package com.apus.salehub.adapter.out.agent.source;

import com.apus.salehub.adapter.out.agent.BaseAgent;
import com.apus.salehub.adapter.out.agent.RateLimiter;
import com.apus.salehub.adapter.out.agent.model.AgentConfig;
import com.apus.salehub.adapter.out.agent.model.RawListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitHubAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(GitHubAgent.class);
    private final RateLimiter limiter = new RateLimiter(10);
    private final WebClient webClient = WebClient.create();

    private static final List<String> SEARCH_QUERIES = List.of(
        "label:help-wanted language:python",
        "label:help-wanted language:javascript",
        "\"looking for developer\" is:issue is:open",
        "\"hiring\" \"freelance\" is:issue is:open",
        "\"need developer\" is:issue is:open",
        "\"bounty\" is:issue is:open label:bounty"
    );

    @Override
    public String getSourceName() { return "github"; }

    @Override
    public String getDisplayName() { return "GitHub"; }

    @Override
    @SuppressWarnings("unchecked")
    public List<RawListing> fetchListings(AgentConfig config) {
        List<RawListing> listings = new ArrayList<>();

        String token = (String) config.getExtra().getOrDefault("github_token", "");

        List<String> queries = new ArrayList<>(SEARCH_QUERIES);
        for (String kw : config.getKeywords()) {
            queries.add("\"" + kw + "\" is:issue is:open label:help-wanted");
        }

        for (String query : queries.subList(0, Math.min(queries.size(), config.getMaxPages()))) {
            try {
                limiter.acquire();

                WebClient.RequestHeadersSpec<?> request = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .scheme("https").host("api.github.com").path("/search/issues")
                        .queryParam("q", query)
                        .queryParam("sort", "created")
                        .queryParam("order", "desc")
                        .queryParam("per_page", 30)
                        .build())
                    .header("Accept", "application/vnd.github.v3+json");

                if (token != null && !token.isBlank()) {
                    request = request.header("Authorization", "token " + token);
                }

                Map<String, Object> data = request.retrieve()
                    .bodyToMono(Map.class).block();

                if (data == null) continue;
                List<Map<String, Object>> items = (List<Map<String, Object>>) data.getOrDefault("items", List.of());

                for (Map<String, Object> item : items) {
                    RawListing listing = parseIssue(item);
                    if (listing != null) listings.add(listing);
                }
            } catch (Exception e) {
                log.error("Error searching GitHub: {}", e.getMessage());
            }
        }

        return listings;
    }

    @SuppressWarnings("unchecked")
    private RawListing parseIssue(Map<String, Object> item) {
        try {
            String title = (String) item.getOrDefault("title", "");
            if (title.isBlank()) return null;

            String body = (String) item.getOrDefault("body", "");
            if (body == null) body = "";
            if (body.length() > 5000) body = body.substring(0, 5000);

            String url = (String) item.getOrDefault("html_url", "");
            Map<String, Object> user = (Map<String, Object>) item.getOrDefault("user", Map.of());

            OffsetDateTime postedAt = null;
            String createdAt = (String) item.get("created_at");
            if (createdAt != null) {
                postedAt = OffsetDateTime.parse(createdAt);
            }

            List<Map<String, Object>> labels = (List<Map<String, Object>>) item.getOrDefault("labels", List.of());
            List<String> labelNames = labels.stream().map(l -> (String) l.getOrDefault("name", "")).toList();

            RawListing listing = new RawListing();
            listing.setExternalId(String.valueOf(item.getOrDefault("id", "")));
            listing.setTitle(title.trim());
            listing.setDescription(body);
            listing.setUrl(url);
            listing.setPostedAt(postedAt);
            listing.setContactName((String) user.getOrDefault("login", null));
            listing.setContactUrl((String) user.getOrDefault("html_url", null));

            Map<String, Object> rawData = new HashMap<>();
            rawData.put("repo", item.getOrDefault("repository_url", ""));
            rawData.put("labels", labelNames);
            rawData.put("comments", item.getOrDefault("comments", 0));
            rawData.put("state", item.getOrDefault("state", null));
            listing.setRawData(rawData);

            return listing;
        } catch (Exception e) {
            log.error("Error parsing GitHub issue: {}", e.getMessage());
            return null;
        }
    }
}

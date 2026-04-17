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

public class RedditAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(RedditAgent.class);
    private final RateLimiter limiter = new RateLimiter(5);
    private final WebClient webClient = WebClient.builder()
        .defaultHeader("User-Agent", "APUS-ProjectFinder/1.0")
        .build();

    private static final List<String> SUBREDDITS = List.of(
        "forhire", "freelance", "hiring", "jobbit", "remotejs", "pythondev"
    );

    @Override
    public String getSourceName() { return "reddit"; }

    @Override
    public String getDisplayName() { return "Reddit"; }

    @Override
    @SuppressWarnings("unchecked")
    public List<RawListing> fetchListings(AgentConfig config) {
        List<RawListing> listings = new ArrayList<>();

        String clientId = (String) config.getExtra().getOrDefault("reddit_client_id", "");
        String clientSecret = (String) config.getExtra().getOrDefault("reddit_client_secret", "");
        String authHeader = null;

        if (clientId != null && !clientId.isBlank() && clientSecret != null && !clientSecret.isBlank()) {
            authHeader = getOAuthToken(clientId, clientSecret);
        }

        for (String subreddit : SUBREDDITS) {
            try {
                limiter.acquire();

                WebClient.RequestHeadersSpec<?> request = webClient.get()
                    .uri("https://www.reddit.com/r/" + subreddit + "/new.json?limit=25&t=week");

                if (authHeader != null) {
                    request = request.header("Authorization", "Bearer " + authHeader);
                }

                Map<String, Object> response = request.retrieve().bodyToMono(Map.class).block();
                if (response == null) continue;

                Map<String, Object> data = (Map<String, Object>) response.getOrDefault("data", Map.of());
                List<Map<String, Object>> children = (List<Map<String, Object>>) data.getOrDefault("children", List.of());

                for (Map<String, Object> child : children) {
                    Map<String, Object> postData = (Map<String, Object>) child.getOrDefault("data", Map.of());
                    RawListing listing = parsePost(postData);
                    if (listing != null && isHiringPost(listing)) {
                        listings.add(listing);
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching r/{}: {}", subreddit, e.getMessage());
            }
        }

        return listings;
    }

    @SuppressWarnings("unchecked")
    private String getOAuthToken(String clientId, String clientSecret) {
        try {
            Map<String, Object> response = WebClient.create()
                .post()
                .uri("https://www.reddit.com/api/v1/access_token")
                .headers(h -> h.setBasicAuth(clientId, clientSecret))
                .header("User-Agent", "APUS-ProjectFinder/1.0")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=client_credentials")
                .retrieve().bodyToMono(Map.class).block();

            return response != null ? (String) response.get("access_token") : null;
        } catch (Exception e) {
            log.error("Failed to get Reddit OAuth token: {}", e.getMessage());
            return null;
        }
    }

    private RawListing parsePost(Map<String, Object> data) {
        try {
            String title = (String) data.getOrDefault("title", "");
            if (title.isBlank()) return null;

            String selfText = (String) data.getOrDefault("selftext", "");
            if (selfText.length() > 5000) selfText = selfText.substring(0, 5000);

            String author = (String) data.getOrDefault("author", "");
            String permalink = (String) data.getOrDefault("permalink", "");

            RawListing listing = new RawListing();
            listing.setExternalId((String) data.getOrDefault("id", ""));
            listing.setTitle(title.trim());
            listing.setDescription(selfText);
            listing.setUrl("https://www.reddit.com" + permalink);
            listing.setContactName(author);
            listing.setContactUrl("https://www.reddit.com/user/" + author);

            Map<String, Object> rawData = new HashMap<>();
            rawData.put("subreddit", data.getOrDefault("subreddit", ""));
            rawData.put("score", data.getOrDefault("score", 0));
            rawData.put("num_comments", data.getOrDefault("num_comments", 0));
            rawData.put("flair", data.getOrDefault("link_flair_text", null));
            listing.setRawData(rawData);

            return listing;
        } catch (Exception e) {
            log.error("Error parsing Reddit post: {}", e.getMessage());
            return null;
        }
    }

    private boolean isHiringPost(RawListing listing) {
        String text = (listing.getTitle() + " " + (listing.getDescription() != null ? listing.getDescription() : "")).toLowerCase();
        List<String> signals = List.of("[hiring]", "looking for", "need a developer", "seeking", "hire");
        return signals.stream().anyMatch(text::contains);
    }
}

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

public class TwitterAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(TwitterAgent.class);
    private final RateLimiter limiter = new RateLimiter(5);
    private final WebClient webClient = WebClient.create();

    private static final List<String> SEARCH_QUERIES = List.of(
        "\"looking for developer\" -is:retweet",
        "\"need a developer\" -is:retweet",
        "\"hiring freelance\" developer -is:retweet",
        "\"outsource\" \"project\" developer -is:retweet",
        "#hiring #developer #freelance -is:retweet"
    );

    @Override
    public String getSourceName() { return "twitter"; }

    @Override
    public String getDisplayName() { return "Twitter/X"; }

    @Override
    @SuppressWarnings("unchecked")
    public List<RawListing> fetchListings(AgentConfig config) {
        String bearerToken = (String) config.getExtra().getOrDefault("twitter_bearer_token", "");
        if (bearerToken == null || bearerToken.isBlank()) {
            log.warn("Twitter bearer token not configured, skipping");
            return List.of();
        }

        List<RawListing> listings = new ArrayList<>();
        List<String> queries = new ArrayList<>(SEARCH_QUERIES);
        for (String kw : config.getKeywords()) {
            queries.add("\"" + kw + "\" \"developer\" -is:retweet");
        }

        for (String query : queries.subList(0, Math.min(queries.size(), config.getMaxPages()))) {
            try {
                limiter.acquire();

                Map<String, Object> data = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .scheme("https").host("api.twitter.com")
                        .path("/2/tweets/search/recent")
                        .queryParam("query", query)
                        .queryParam("max_results", 20)
                        .queryParam("tweet.fields", "created_at,author_id,text")
                        .queryParam("expansions", "author_id")
                        .queryParam("user.fields", "name,username")
                        .build())
                    .header("Authorization", "Bearer " + bearerToken)
                    .retrieve().bodyToMono(Map.class).block();

                if (data == null) continue;

                Map<String, Object> includes = (Map<String, Object>) data.getOrDefault("includes", Map.of());
                List<Map<String, Object>> users = (List<Map<String, Object>>) includes.getOrDefault("users", List.of());
                Map<String, Map<String, Object>> userMap = new HashMap<>();
                for (Map<String, Object> u : users) {
                    userMap.put((String) u.get("id"), u);
                }

                List<Map<String, Object>> tweets = (List<Map<String, Object>>) data.getOrDefault("data", List.of());
                for (Map<String, Object> tweet : tweets) {
                    RawListing listing = parseTweet(tweet, userMap);
                    if (listing != null) listings.add(listing);
                }
            } catch (Exception e) {
                log.error("Error searching Twitter: {}", e.getMessage());
            }
        }

        return listings;
    }

    private RawListing parseTweet(Map<String, Object> tweet, Map<String, Map<String, Object>> users) {
        try {
            String text = (String) tweet.getOrDefault("text", "");
            if (text.length() < 30) return null;

            String tweetId = (String) tweet.getOrDefault("id", "");
            String authorId = (String) tweet.getOrDefault("author_id", "");
            Map<String, Object> user = users.getOrDefault(authorId, Map.of());
            String username = (String) user.getOrDefault("username", "");

            RawListing listing = new RawListing();
            listing.setExternalId(tweetId);
            listing.setTitle(text.length() > 100 ? text.substring(0, 100) : text);
            listing.setDescription(text);
            listing.setUrl(!username.isBlank() ? "https://twitter.com/" + username + "/status/" + tweetId : null);
            listing.setContactName((String) user.getOrDefault("name", null));
            listing.setContactUrl(!username.isBlank() ? "https://twitter.com/" + username : null);
            listing.setRawData(Map.of(
                "tweet_id", tweetId,
                "author", username,
                "full_text", text
            ));
            return listing;
        } catch (Exception e) {
            log.error("Error parsing tweet: {}", e.getMessage());
            return null;
        }
    }
}

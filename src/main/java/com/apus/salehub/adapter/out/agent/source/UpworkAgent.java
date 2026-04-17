package com.apus.salehub.adapter.out.agent.source;

import com.apus.salehub.adapter.out.agent.BaseAgent;
import com.apus.salehub.adapter.out.agent.RateLimiter;
import com.apus.salehub.adapter.out.agent.model.AgentConfig;
import com.apus.salehub.adapter.out.agent.model.RawListing;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.StringReader;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UpworkAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(UpworkAgent.class);
    private final RateLimiter limiter = new RateLimiter(5);
    private final WebClient webClient = WebClient.create();

    @Override
    public String getSourceName() { return "upwork"; }

    @Override
    public String getDisplayName() { return "Upwork"; }

    @Override
    @SuppressWarnings("unchecked")
    public List<RawListing> fetchListings(AgentConfig config) {
        List<RawListing> listings = new ArrayList<>();
        List<String> feeds = new ArrayList<>();

        Object rssFeeds = config.getExtra().get("rss_feeds");
        if (rssFeeds instanceof List) {
            feeds.addAll((List<String>) rssFeeds);
        }

        if (feeds.isEmpty() && !config.getKeywords().isEmpty()) {
            for (String kw : config.getKeywords()) {
                feeds.add("https://www.upwork.com/ab/feed/jobs/rss?q=" +
                    kw.replace(" ", "+") + "&sort=recency");
            }
        }

        if (feeds.isEmpty()) {
            feeds.add("https://www.upwork.com/ab/feed/jobs/rss?q=python+developer&sort=recency");
            feeds.add("https://www.upwork.com/ab/feed/jobs/rss?q=react+developer&sort=recency");
            feeds.add("https://www.upwork.com/ab/feed/jobs/rss?q=mobile+app&sort=recency");
        }

        for (String feedUrl : feeds) {
            try {
                limiter.acquire();
                String xml = webClient.get().uri(feedUrl)
                    .retrieve().bodyToMono(String.class).block();

                if (xml == null) continue;
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new StringReader(xml));

                for (SyndEntry entry : feed.getEntries()) {
                    RawListing listing = parseEntry(entry);
                    if (listing != null && !shouldExclude(listing, config)) {
                        listings.add(listing);
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching Upwork feed {}: {}", feedUrl, e.getMessage());
            }
        }

        return listings;
    }

    private RawListing parseEntry(SyndEntry entry) {
        try {
            String title = entry.getTitle();
            if (title == null || title.isBlank()) return null;

            String description = entry.getDescription() != null ? entry.getDescription().getValue() : "";
            String link = entry.getLink();

            String budgetType = null;
            if (description.toLowerCase().contains("hourly")) budgetType = "hourly";
            else if (description.toLowerCase().contains("fixed") || description.toLowerCase().contains("budget"))
                budgetType = "fixed";

            OffsetDateTime postedAt = null;
            Date published = entry.getPublishedDate();
            if (published != null) {
                postedAt = published.toInstant().atOffset(ZoneOffset.UTC);
            }

            RawListing listing = new RawListing();
            listing.setExternalId(link != null ? link : title);
            listing.setTitle(title.trim());
            listing.setDescription(description);
            listing.setUrl(link);
            listing.setBudgetType(budgetType);
            listing.setPostedAt(postedAt);
            listing.setRawData(Map.of(
                "title", title,
                "link", link != null ? link : "",
                "description", description
            ));
            return listing;
        } catch (Exception e) {
            log.error("Error parsing Upwork entry: {}", e.getMessage());
            return null;
        }
    }

    private boolean shouldExclude(RawListing listing, AgentConfig config) {
        if (config.getExcludeKeywords().isEmpty()) return false;
        String text = (listing.getTitle() + " " + (listing.getDescription() != null ? listing.getDescription() : "")).toLowerCase();
        return config.getExcludeKeywords().stream().anyMatch(kw -> text.contains(kw.toLowerCase()));
    }
}

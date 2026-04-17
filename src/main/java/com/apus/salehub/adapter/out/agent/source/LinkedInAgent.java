package com.apus.salehub.adapter.out.agent.source;

import com.apus.salehub.adapter.out.agent.BaseAgent;
import com.apus.salehub.adapter.out.agent.RateLimiter;
import com.apus.salehub.adapter.out.agent.model.AgentConfig;
import com.apus.salehub.adapter.out.agent.model.RawListing;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LinkedInAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(LinkedInAgent.class);
    private final RateLimiter limiter = new RateLimiter(3);
    private final WebClient webClient = WebClient.builder()
        .defaultHeader("User-Agent",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        .build();

    @Override
    public String getSourceName() { return "linkedin"; }

    @Override
    public String getDisplayName() { return "LinkedIn"; }

    @Override
    public List<RawListing> fetchListings(AgentConfig config) {
        List<RawListing> listings = new ArrayList<>();
        List<String> keywords = config.getKeywords().isEmpty()
            ? List.of("outsource software development", "freelance developer")
            : config.getKeywords();

        for (String keyword : keywords.subList(0, Math.min(keywords.size(), config.getMaxPages()))) {
            try {
                limiter.acquire();

                String html = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .scheme("https").host("www.linkedin.com").path("/jobs/search")
                        .queryParam("keywords", keyword)
                        .queryParam("location", "Worldwide")
                        .queryParam("sortBy", "DD")
                        .queryParam("f_TPR", "r86400")
                        .build())
                    .retrieve().bodyToMono(String.class).block();

                if (html == null) continue;
                Document doc = Jsoup.parse(html);
                Elements cards = doc.select("div.base-card");

                for (Element card : cards) {
                    if (listings.size() >= 20) break;
                    RawListing listing = parseCard(card);
                    if (listing != null) listings.add(listing);
                }
            } catch (Exception e) {
                log.error("Error searching LinkedIn: {}", e.getMessage());
            }
        }

        return listings;
    }

    private RawListing parseCard(Element card) {
        try {
            Element titleEl = card.selectFirst("h3.base-search-card__title");
            String title = titleEl != null ? titleEl.text().trim() : null;
            if (title == null || title.isBlank()) return null;

            Element companyEl = card.selectFirst("h4.base-search-card__subtitle");
            String company = companyEl != null ? companyEl.text().trim() : null;

            Element linkEl = card.selectFirst("a.base-card__full-link");
            String url = linkEl != null ? linkEl.attr("href") : null;

            Element locationEl = card.selectFirst("span.job-search-card__location");
            String location = locationEl != null ? locationEl.text().trim() : null;

            RawListing listing = new RawListing();
            listing.setExternalId(url != null ? url : title);
            listing.setTitle(title);
            listing.setDescription("Company: " + company + "\nLocation: " + location);
            listing.setUrl(url);
            listing.setContactCompany(company);
            listing.setRawData(Map.of(
                "company", company != null ? company : "",
                "location", location != null ? location : "",
                "platform", "linkedin"
            ));
            return listing;
        } catch (Exception e) {
            log.error("Error parsing LinkedIn card: {}", e.getMessage());
            return null;
        }
    }
}

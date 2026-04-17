package com.apus.salehub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "apus")
public class AppProperties {

    private String anthropicApiKey = "";
    private int scrapeIntervalMinutes = 60;
    private int scoreThresholdNotify = 75;
    private int teamHourlyRateMin = 30;
    private int teamHourlyRateMax = 80;
    private List<String> teamSkills = new ArrayList<>();
    private String githubToken = "";
    private String twitterBearerToken = "";
    private String redditClientId = "";
    private String redditClientSecret = "";
    private String linkedinCookie = "";
    private List<String> upworkRssFeeds = new ArrayList<>();

}

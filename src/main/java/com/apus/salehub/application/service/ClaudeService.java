package com.apus.salehub.application.service;

import com.apus.salehub.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ClaudeService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeService.class);
    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-sonnet-4-20250514";

    private final WebClient webClient;
    private final AppProperties properties;

    public ClaudeService(WebClient.Builder webClientBuilder, AppProperties properties) {
        this.webClient = webClientBuilder.baseUrl(API_URL).build();
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> analyzeProject(String title, String description) {
        String prompt = """
            Analyze this outsourcing project opportunity:

            Title: %s
            Description: %s

            Extract and return as JSON:
            1. "summary": 2-3 sentence summary
            2. "skills": list of required technical skills
            3. "project_type": one of [web_app, mobile_app, api, data, ml, devops, design, other]
            4. "complexity": one of [simple, moderate, complex]
            5. "estimated_hours": rough estimate range [min, max]
            6. "red_flags": any concerns about the project
            7. "highlights": positive aspects
            """.formatted(title, description);

        try {
            Map<String, Object> response = callClaude(prompt, 1024);
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            if (content != null && !content.isEmpty()) {
                return Map.of("raw_response", content.get(0).get("text"));
            }
        } catch (Exception e) {
            log.error("Failed to analyze project: {}", e.getMessage());
        }
        return Map.of("raw_response", "");
    }

    @SuppressWarnings("unchecked")
    public String generateProposal(String projectTitle, String projectDescription,
                                    String template, String portfolio) {
        String prompt = """
            Generate a professional proposal for this outsourcing project.

            PROJECT:
            Title: %s
            Description: %s

            TEMPLATE STYLE:
            %s

            OUR PORTFOLIO:
            %s

            Write a compelling proposal that:
            1. Shows understanding of the client's needs
            2. Proposes a clear solution approach
            3. Highlights relevant experience
            4. Includes a realistic timeline
            5. Maintains a professional but friendly tone
            """.formatted(projectTitle, projectDescription, template, portfolio);

        try {
            Map<String, Object> response = callClaude(prompt, 2048);
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            if (content != null && !content.isEmpty()) {
                return (String) content.get(0).get("text");
            }
        } catch (Exception e) {
            log.error("Failed to generate proposal: {}", e.getMessage());
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callClaude(String prompt, int maxTokens) {
        Map<String, Object> body = Map.of(
            "model", MODEL,
            "max_tokens", maxTokens,
            "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        return webClient.post()
            .header("x-api-key", properties.getAnthropicApiKey())
            .header("anthropic-version", "2023-06-01")
            .header("Content-Type", "application/json")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
    }
}

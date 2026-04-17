package com.apus.salehub.adapter.out.agent.processor;

public final class Normalizer {

    private Normalizer() {}

    public static String cleanHtml(String text) {
        if (text == null || text.isBlank()) return "";
        // Remove HTML tags
        String cleaned = text.replaceAll("<[^>]+>", " ");
        // Decode HTML entities
        cleaned = unescapeHtml(cleaned);
        // Collapse whitespace
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        return cleaned;
    }

    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) return text != null ? text : "";
        return text.substring(0, maxLength) + "...";
    }

    public static String truncate(String text) {
        return truncate(text, 5000);
    }

    public static String normalizeUrl(String url) {
        if (url == null || url.isBlank()) return null;
        url = url.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        return url;
    }

    private static String unescapeHtml(String text) {
        // Basic HTML entity decoding
        return text
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ");
    }
}

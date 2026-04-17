package com.apus.salehub.adapter.out.agent.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContactExtractor {

    private ContactExtractor() {}

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");
    private static final Pattern URL_PATTERN =
        Pattern.compile("https?://[^\\s<>\"']+");
    private static final Pattern DISCORD_PATTERN =
        Pattern.compile("(?:discord|disc)\\s*[:#]?\\s*(\\S+#\\d{4}|\\S+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TELEGRAM_PATTERN =
        Pattern.compile("(?:telegram|tg)\\s*[:#@]?\\s*@?(\\w+)", Pattern.CASE_INSENSITIVE);

    public static String extractEmail(String text) {
        if (text == null) return null;
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        return matcher.find() ? matcher.group(0) : null;
    }

    public static Map<String, String> extractContactInfo(String text) {
        Map<String, String> info = new HashMap<>();
        info.put("email", extractEmail(text));

        if (text != null) {
            Matcher urlMatcher = URL_PATTERN.matcher(text);
            info.put("website", urlMatcher.find() ? urlMatcher.group(0) : null);

            Matcher discordMatcher = DISCORD_PATTERN.matcher(text);
            info.put("discord", discordMatcher.find() ? discordMatcher.group(1) : null);

            Matcher telegramMatcher = TELEGRAM_PATTERN.matcher(text);
            info.put("telegram", telegramMatcher.find() ? telegramMatcher.group(1) : null);
        }

        return info;
    }
}

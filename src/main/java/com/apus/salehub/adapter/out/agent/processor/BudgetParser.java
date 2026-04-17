package com.apus.salehub.adapter.out.agent.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BudgetParser {

    private BudgetParser() {}

    private static final Pattern DOLLAR_PATTERN = Pattern.compile("\\$[\\d,]+(?:\\.\\d{2})?");
    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d[\\d,]*)\\s*[-\\u2013to]+\\s*(\\d[\\d,]*)");

    public record BudgetInfo(Double min, Double max, String type) {}

    public static BudgetInfo parseBudget(String text) {
        if (text == null || text.isBlank()) return new BudgetInfo(null, null, null);

        String textLower = text.toLowerCase();

        // Detect type
        String budgetType = null;
        if (textLower.contains("per hour") || textLower.contains("/hr") ||
            textLower.contains("hourly") || textLower.contains("/hour")) {
            budgetType = "hourly";
        } else if (textLower.contains("per month") || textLower.contains("monthly") ||
                   textLower.contains("/month")) {
            budgetType = "monthly";
        } else if (textLower.contains("fixed") || textLower.contains("total") ||
                   textLower.contains("budget") || textLower.contains("project")) {
            budgetType = "fixed";
        }

        // Extract dollar amounts
        List<Double> numbers = new ArrayList<>();
        Matcher dollarMatcher = DOLLAR_PATTERN.matcher(text);
        while (dollarMatcher.find()) {
            String cleaned = dollarMatcher.group().replace("$", "").replace(",", "");
            try {
                numbers.add(Double.parseDouble(cleaned));
            } catch (NumberFormatException ignored) {}
        }

        if (numbers.isEmpty()) {
            Matcher rangeMatcher = RANGE_PATTERN.matcher(text);
            if (rangeMatcher.find()) {
                try {
                    numbers.add(Double.parseDouble(rangeMatcher.group(1).replace(",", "")));
                    numbers.add(Double.parseDouble(rangeMatcher.group(2).replace(",", "")));
                } catch (NumberFormatException ignored) {}
            }
        }

        if (numbers.isEmpty()) return new BudgetInfo(null, null, budgetType);

        Collections.sort(numbers);
        if (numbers.size() == 1) {
            return new BudgetInfo(numbers.get(0), numbers.get(0), budgetType != null ? budgetType : "fixed");
        }
        return new BudgetInfo(numbers.get(0), numbers.get(numbers.size() - 1), budgetType != null ? budgetType : "fixed");
    }
}

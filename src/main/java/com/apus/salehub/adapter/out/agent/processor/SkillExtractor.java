package com.apus.salehub.adapter.out.agent.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class SkillExtractor {

    private SkillExtractor() {}

    private static final Map<String, Pattern> SKILL_PATTERNS = new LinkedHashMap<>();

    static {
        // Languages
        SKILL_PATTERNS.put("Python", Pattern.compile("\\bpython\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("JavaScript", Pattern.compile("\\bjavascript\\b|\\bjs\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("TypeScript", Pattern.compile("\\btypescript\\b|\\bts\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Java", Pattern.compile("\\bjava\\b(?!script)", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Go", Pattern.compile("\\bgolang\\b|\\bgo\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Rust", Pattern.compile("\\brust\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("PHP", Pattern.compile("\\bphp\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Ruby", Pattern.compile("\\bruby\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Swift", Pattern.compile("\\bswift\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Kotlin", Pattern.compile("\\bkotlin\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("C#", Pattern.compile("\\bc#\\b|\\.net\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("C++", Pattern.compile("\\bc\\+\\+\\b", Pattern.CASE_INSENSITIVE));
        // Frontend
        SKILL_PATTERNS.put("React", Pattern.compile("\\breact\\b|\\breactjs\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Next.js", Pattern.compile("\\bnext\\.?js\\b|\\bnextjs\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Vue", Pattern.compile("\\bvue\\b|\\bvuejs\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Angular", Pattern.compile("\\bangular\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Svelte", Pattern.compile("\\bsvelte\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Tailwind", Pattern.compile("\\btailwind\\b", Pattern.CASE_INSENSITIVE));
        // Backend
        SKILL_PATTERNS.put("FastAPI", Pattern.compile("\\bfastapi\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Django", Pattern.compile("\\bdjango\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Flask", Pattern.compile("\\bflask\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Express", Pattern.compile("\\bexpress\\b|\\bexpressjs\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Node.js", Pattern.compile("\\bnode\\.?js\\b|\\bnodejs\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Spring", Pattern.compile("\\bspring\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Laravel", Pattern.compile("\\blaravel\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Rails", Pattern.compile("\\brails\\b|\\bruby on rails\\b", Pattern.CASE_INSENSITIVE));
        // Mobile
        SKILL_PATTERNS.put("React Native", Pattern.compile("\\breact.native\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Flutter", Pattern.compile("\\bflutter\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("iOS", Pattern.compile("\\bios\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Android", Pattern.compile("\\bandroid\\b", Pattern.CASE_INSENSITIVE));
        // Data/ML
        SKILL_PATTERNS.put("Machine Learning", Pattern.compile("\\bmachine.learning\\b|\\bml\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("AI", Pattern.compile("\\bartificial.intelligence\\b|\\b(?:gen)?ai\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Data Science", Pattern.compile("\\bdata.science\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("TensorFlow", Pattern.compile("\\btensorflow\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("PyTorch", Pattern.compile("\\bpytorch\\b", Pattern.CASE_INSENSITIVE));
        // Database
        SKILL_PATTERNS.put("PostgreSQL", Pattern.compile("\\bpostgres(?:ql)?\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("MongoDB", Pattern.compile("\\bmongodb\\b|\\bmongo\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("MySQL", Pattern.compile("\\bmysql\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Redis", Pattern.compile("\\bredis\\b", Pattern.CASE_INSENSITIVE));
        // Cloud/DevOps
        SKILL_PATTERNS.put("AWS", Pattern.compile("\\baws\\b|\\bamazon.web.services\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("GCP", Pattern.compile("\\bgcp\\b|\\bgoogle.cloud\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Azure", Pattern.compile("\\bazure\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Docker", Pattern.compile("\\bdocker\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Kubernetes", Pattern.compile("\\bkubernetes\\b|\\bk8s\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("CI/CD", Pattern.compile("\\bci/?cd\\b", Pattern.CASE_INSENSITIVE));
        // Other
        SKILL_PATTERNS.put("GraphQL", Pattern.compile("\\bgraphql\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("REST API", Pattern.compile("\\brest\\b.*\\bapi\\b|\\brestful\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("WebSocket", Pattern.compile("\\bwebsocket\\b", Pattern.CASE_INSENSITIVE));
        SKILL_PATTERNS.put("Blockchain", Pattern.compile("\\bblockchain\\b|\\bweb3\\b", Pattern.CASE_INSENSITIVE));
    }

    public static List<String> extractSkills(String text) {
        if (text == null || text.isBlank()) return List.of();

        List<String> found = new ArrayList<>();
        for (Map.Entry<String, Pattern> entry : SKILL_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(text).find()) {
                found.add(entry.getKey());
            }
        }
        return found;
    }
}

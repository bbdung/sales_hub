package com.apus.salehub.config.tenant;

import org.slf4j.MDC;

import java.util.UUID;

public final class CorrelationContext {

    public static final String MDC_KEY = "correlationId";
    private static final String TRACE_ID_KEY = "traceId";

    private CorrelationContext() {
    }

    public static String getOrGenerate() {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId != null && !traceId.isBlank()) {
            MDC.put(MDC_KEY, traceId);
            return traceId;
        }

        String correlationId = MDC.get(MDC_KEY);
        if (correlationId != null && !correlationId.isBlank()) {
            return correlationId;
        }

        correlationId = generate();
        MDC.put(MDC_KEY, correlationId);
        return correlationId;
    }

    public static String get() {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId != null && !traceId.isBlank()) {
            return traceId;
        }
        return MDC.get(MDC_KEY);
    }

    public static void set(String correlationId) {
        MDC.put(MDC_KEY, correlationId);
    }

    public static void clear() {
        MDC.remove(MDC_KEY);
    }

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}

package com.apus.salehub.config.tenant.event;

import com.apus.base.tenant.TenantContext;
import com.apus.salehub.config.tenant.CorrelationContext;

import java.time.Instant;
import java.util.UUID;

public record DomainEventEnvelope<T>(
        String eventId,
        String eventType,
        String tenantId,
        String correlationId,
        String source,
        Instant timestamp,
        T payload
) {

    public static <T> DomainEventEnvelope<T> wrap(T payload, String eventType, String source) {
        return new DomainEventEnvelope<>(
                UUID.randomUUID().toString(),
                eventType,
                TenantContext.getTenantId(),
                CorrelationContext.getOrGenerate(),
                source,
                Instant.now(),
                payload
        );
    }
}

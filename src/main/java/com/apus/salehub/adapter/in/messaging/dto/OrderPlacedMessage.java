package com.apus.salehub.adapter.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Inbound DTO for events received from pos_service's {@code pos.order.placed} topic.
 * Uses {@code @JsonIgnoreProperties(ignoreUnknown = true)} for forward-compatible
 * deserialization: new fields added by the producer will be silently ignored (FR-008).
 * <p>
 * Fields will be expanded when pos_service order domain is fully implemented.
 * <p>
 * Note: {@code occurredAt} is stored as String to avoid requiring jackson-datatype-jsr310.
 * The consumer does not need to parse the timestamp; it is used for logging/diagnostics only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderPlacedMessage(
        String orderId,
        String occurredAt
) {
}

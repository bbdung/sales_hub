package com.apus.salehub.adapter.in.messaging.consumer;

import com.apus.base.tenant.TenantContext;
import com.apus.salehub.config.tenant.CorrelationContext;
import com.apus.salehub.config.tenant.kafka.KafkaMetrics;
import io.micrometer.core.instrument.Timer;
import com.apus.salehub.adapter.in.messaging.dto.OrderPlacedMessage;
import com.apus.salehub.application.port.in.messaging.OrderPlacedEventHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer adapter that listens to {@code pos.order.placed} topic
 * and delegates to {@link OrderPlacedEventHandler} for stock reservation logic.
 * <p>
 * Tenant and correlation context are automatically restored by
 * {@code TenantConsumerInterceptor} before this method is invoked.
 * <p>
 * Uses {@link JsonNode} to parse the envelope to avoid needing Jackson java.time
 * module for the envelope's {@code Instant timestamp} field. Only the payload
 * is deserialized into the typed DTO.
 */
@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class StockReservationConsumer {

    private static final Logger log = LoggerFactory.getLogger(StockReservationConsumer.class);
    private static final String TOPIC = "pos.order.placed";
    private static final String EVENT_TYPE = "pos.OrderPlacedEvent";
    private static final String SERVICE = "warehouse-service";

    private final OrderPlacedEventHandler handler;
    private final JsonMapper jsonMapper;
    private final KafkaMetrics kafkaMetrics;

    public StockReservationConsumer(OrderPlacedEventHandler handler, JsonMapper jsonMapper,
                                    KafkaMetrics kafkaMetrics) {
        this.handler = handler;
        this.jsonMapper = jsonMapper;
        this.kafkaMetrics = kafkaMetrics;
    }

    @KafkaListener(topics = TOPIC, groupId = "warehouse-service-group")
    public void consume(String message) {
        String correlationId = CorrelationContext.get();
        String tenantId = String.valueOf(TenantContext.getTenantId());
        Timer.Sample timerSample = kafkaMetrics.startConsumeTimer();

        log.info("[KAFKA-CONSUME] correlationId={} tenantId={} eventType={} topic={}",
                correlationId, tenantId, EVENT_TYPE, TOPIC);

        try {
            JsonNode root = jsonMapper.readTree(message);
            String eventId = root.path("eventId").asText();
            JsonNode payloadNode = root.path("payload");

            OrderPlacedMessage payload = jsonMapper.treeToValue(payloadNode, OrderPlacedMessage.class);

            handler.handle(payload);

            kafkaMetrics.recordConsume(TOPIC, EVENT_TYPE, SERVICE);
            timerSample.stop(kafkaMetrics.consumeTimer(TOPIC, EVENT_TYPE, SERVICE));
            log.info("[KAFKA-CONSUME-OK] correlationId={} tenantId={} eventType={} eventId={}",
                    correlationId, tenantId, EVENT_TYPE, eventId);
        } catch (Exception e) {
            kafkaMetrics.recordConsumeError(TOPIC, EVENT_TYPE, SERVICE);
            timerSample.stop(kafkaMetrics.consumeTimer(TOPIC, EVENT_TYPE, SERVICE));
            log.error("[KAFKA-CONSUME-FAIL] correlationId={} tenantId={} eventType={} error={}",
                    correlationId, tenantId, EVENT_TYPE, e.getMessage(), e);
            throw new RuntimeException("Failed to process OrderPlacedEvent", e);
        }
    }
}

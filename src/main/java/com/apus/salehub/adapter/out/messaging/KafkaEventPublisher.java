package com.apus.salehub.adapter.out.messaging;

import com.apus.salehub.application.port.out.messaging.EventPublisher;
import com.apus.salehub.config.tenant.event.DomainEventEnvelope;
import com.apus.salehub.config.tenant.kafka.KafkaMetrics;
import com.apus.salehub.domain.event.LowStockAlertEvent;
import com.apus.salehub.domain.event.StockMovementRecordedEvent;
import com.apus.salehub.domain.event.StockUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@Primary
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class KafkaEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;
    private final KafkaMetrics kafkaMetrics;
    private final String serviceName;
    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                               JsonMapper jsonMapper,
                               KafkaMetrics kafkaMetrics,
                               @Value("${spring.application.name}") String serviceName) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
        this.kafkaMetrics = kafkaMetrics;
        this.serviceName = serviceName;
    }

    @Override
    public void publish(StockUpdatedEvent event) {
        send("inventory.stock.updated", event, "warehouse.StockUpdatedEvent");
    }

    @Override
    public void publish(LowStockAlertEvent event) {
        send("inventory.stock.low-alert", event, "warehouse.LowStockAlertEvent");
    }

    @Override
    public void publish(StockMovementRecordedEvent event) {
        send("inventory.movement.recorded", event, "warehouse.StockMovementRecordedEvent");
    }

    private <T> void send(String topic, T event, String eventType) {
        var envelope = DomainEventEnvelope.wrap(event, eventType, serviceName);
        try {
            String json = jsonMapper.writeValueAsString(envelope);
            kafkaTemplate.send(topic, envelope.eventId(), json);
            kafkaMetrics.recordPublish(topic, eventType, serviceName);
            log.info("[KAFKA-PUB] correlationId={} topic={} eventType={} tenantId={} eventId={}",
                    envelope.correlationId(), topic, eventType, envelope.tenantId(), envelope.eventId());
        } catch (Exception e) {
            kafkaMetrics.recordPublishError(topic, eventType, serviceName);
            log.error("[KAFKA-PUB-FAIL] correlationId={} topic={} eventType={} tenantId={} error={}",
                    envelope.correlationId(), topic, eventType, envelope.tenantId(), e.getMessage(), e);
        }
    }
}

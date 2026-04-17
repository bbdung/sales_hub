package com.apus.salehub.config.tenant.kafka;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class KafkaMetrics {

    private final MeterRegistry registry;

    public KafkaMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void recordPublish(String topic, String eventType, String service) {
        Counter.builder("kafka.events.published")
                .tag("topic", topic)
                .tag("eventType", eventType)
                .tag("service", service)
                .register(registry)
                .increment();
    }

    public void recordPublishError(String topic, String eventType, String service) {
        Counter.builder("kafka.events.publish.errors")
                .tag("topic", topic)
                .tag("eventType", eventType)
                .tag("service", service)
                .register(registry)
                .increment();
    }

    public void recordConsume(String topic, String eventType, String service) {
        Counter.builder("kafka.events.consumed")
                .tag("topic", topic)
                .tag("eventType", eventType)
                .tag("service", service)
                .register(registry)
                .increment();
    }

    public void recordConsumeError(String topic, String eventType, String service) {
        Counter.builder("kafka.events.consume.errors")
                .tag("topic", topic)
                .tag("eventType", eventType)
                .tag("service", service)
                .register(registry)
                .increment();
    }

    public void recordDlt(String originalTopic, String eventType, String service) {
        Counter.builder("kafka.events.dlt")
                .tag("topic", originalTopic)
                .tag("eventType", eventType)
                .tag("service", service)
                .register(registry)
                .increment();
    }

    public Timer.Sample startConsumeTimer() {
        return Timer.start(registry);
    }

    public Timer consumeTimer(String topic, String eventType, String service) {
        return Timer.builder("kafka.consumer.processing.latency")
                .tag("topic", topic)
                .tag("eventType", eventType)
                .tag("service", service)
                .register(registry);
    }
}

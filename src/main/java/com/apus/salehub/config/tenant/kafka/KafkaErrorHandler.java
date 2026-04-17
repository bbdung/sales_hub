package com.apus.salehub.config.tenant.kafka;

import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

public final class KafkaErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(KafkaErrorHandler.class);
    private static final long DEFAULT_BACKOFF_INTERVAL_MS = 1000L;
    private static final long DEFAULT_MAX_RETRIES = 2L;
    private static final String DLT_SUFFIX = ".DLT";

    private KafkaErrorHandler() {
    }

    public static CommonErrorHandler create(KafkaOperations<String, String> kafkaTemplate) {
        return create(kafkaTemplate, DEFAULT_BACKOFF_INTERVAL_MS, DEFAULT_MAX_RETRIES);
    }

    public static CommonErrorHandler create(KafkaOperations<String, String> kafkaTemplate,
                                            long backoffMs, long maxRetries) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> {
                    log.error("[KAFKA-DLT] topic={} originalTopic={} partition={} offset={} error={}",
                            record.topic() + DLT_SUFFIX, record.topic(), record.partition(),
                            record.offset(), ex.getMessage());
                    return new TopicPartition(record.topic() + DLT_SUFFIX, record.partition());
                });

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer,
                new FixedBackOff(backoffMs, maxRetries));

        handler.addNotRetryableExceptions(IllegalArgumentException.class);

        log.info("Kafka error handler configured: {}ms backoff, {} retries, DLT suffix='{}'",
                backoffMs, maxRetries, DLT_SUFFIX);

        return handler;
    }
}

package com.apus.salehub.config.tenant.kafka;

import com.apus.base.tenant.TenantContext;
import com.apus.salehub.config.tenant.CorrelationContext;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.RecordInterceptor;

import java.nio.charset.StandardCharsets;

public class TenantConsumerInterceptor implements RecordInterceptor<String, String> {

    private static final Logger log = LoggerFactory.getLogger(TenantConsumerInterceptor.class);

    @Override
    public ConsumerRecord<String, String> intercept(ConsumerRecord<String, String> record,
                                                    Consumer<String, String> consumer) {
        String tenantIdStr = extractHeader(record, KafkaHeaderKeys.TENANT_ID);
        String correlationId = extractHeader(record, KafkaHeaderKeys.CORRELATION_ID);

        if (tenantIdStr != null && !tenantIdStr.isBlank()) {
            try {
                TenantContext.setTenantId(tenantIdStr);
            } catch (NumberFormatException e) {
                log.warn("[KAFKA-IN] Invalid x-tenant-id header: {} topic={} partition={} offset={}",
                        tenantIdStr, record.topic(), record.partition(), record.offset());
                throw new IllegalArgumentException("Invalid x-tenant-id header: " + tenantIdStr, e);
            }
        }

        if (correlationId != null && !correlationId.isBlank()) {
            CorrelationContext.set(correlationId);
        }
        CorrelationContext.getOrGenerate();

        log.info("[KAFKA-IN] correlationId={} topic={} partition={} offset={} tenantId={}",
                CorrelationContext.get(), record.topic(), record.partition(), record.offset(), tenantIdStr);

        return record;
    }

    @Override
    public void afterRecord(ConsumerRecord<String, String> record, Consumer<String, String> consumer) {
        TenantContext.clear();
        CorrelationContext.clear();
    }

    private String extractHeader(ConsumerRecord<String, String> record, String key) {
        Header header = record.headers().lastHeader(key);
        if (header == null || header.value() == null) {
            return null;
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }
}

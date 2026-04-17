package com.apus.salehub.config.tenant.kafka;

import com.apus.base.tenant.TenantContext;
import com.apus.salehub.config.tenant.CorrelationContext;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class TenantProducerInterceptor implements ProducerInterceptor<String, String> {

    private static final Logger log = LoggerFactory.getLogger(TenantProducerInterceptor.class);

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        Long tenantId = Objects.nonNull(TenantContext.getTenantId()) ?
                Long.valueOf(TenantContext.getTenantId()) : null;
        if (tenantId != null) {
            record.headers().add(KafkaHeaderKeys.TENANT_ID,
                    tenantId.toString().getBytes(StandardCharsets.UTF_8));
        }

        String correlationId = CorrelationContext.getOrGenerate();
        record.headers().add(KafkaHeaderKeys.CORRELATION_ID,
                correlationId.getBytes(StandardCharsets.UTF_8));

        log.info("[KAFKA-OUT] correlationId={} topic={} tenantId={}",
                correlationId, record.topic(), tenantId);

        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}

package com.apus.salehub.config;

import com.apus.salehub.application.port.out.messaging.EventPublisher;
import com.apus.salehub.domain.event.LowStockAlertEvent;
import com.apus.salehub.domain.event.StockMovementRecordedEvent;
import com.apus.salehub.domain.event.StockUpdatedEvent;
import com.apus.salehub.domain.service.InventoryDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class BeansConfig {

    private static final Logger log = LoggerFactory.getLogger(BeansConfig.class);

    @Bean
    public InventoryDomainService inventoryDomainService() {
        return new InventoryDomainService();
    }

    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder().build();
    }

    @Bean
    public EventPublisher noOpEventPublisher() {
        return new EventPublisher() {
            @Override
            public void publish(StockUpdatedEvent event) {
                log.debug("[NO-OP] StockUpdatedEvent ignored");
            }

            @Override
            public void publish(LowStockAlertEvent event) {
                log.debug("[NO-OP] LowStockAlertEvent ignored");
            }

            @Override
            public void publish(StockMovementRecordedEvent event) {
                log.debug("[NO-OP] StockMovementRecordedEvent ignored");
            }
        };
    }
}

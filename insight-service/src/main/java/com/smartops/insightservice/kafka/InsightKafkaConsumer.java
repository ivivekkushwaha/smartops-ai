package com.smartops.insightservice.kafka;

import com.smartops.common.event.AlertEvent;
import com.smartops.insightservice.model.Insight;
import com.smartops.insightservice.service.InsightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
@Slf4j
@RequiredArgsConstructor
public class InsightKafkaConsumer {

    private final InsightService insightService;

    @KafkaListener(
            topics = "alerts-topic",
            groupId = "insight-group"
    )
    public void consume(AlertEvent event) {

        if (event.getUserId() == null || event.getUserId().isBlank()) {
            log.warn("Discarding ownerless alert event");
            return;
        }

        if (!"CRITICAL".equalsIgnoreCase(event.getSeverity())) {
            return;
        }

        Insight savedInsight = insightService.saveInsight(event);

        log.info("Insight saved successfully: {}", savedInsight.getId());
    }
}
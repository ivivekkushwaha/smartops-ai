package com.smartops.monitor.kafka;

import com.smartops.common.event.LogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.smartops.common.event.AlertEvent;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    // ONE GENERIC TEMPLATE
    private final KafkaTemplate<String, Object>
            kafkaTemplate;

    private static final String ALERT_TOPIC = "alerts-topic";
    public void sendAlert(AlertEvent event) {

        kafkaTemplate.send(
                ALERT_TOPIC,
                event.getUserId(),
                event
        );
    }
}

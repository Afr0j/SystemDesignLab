package com.systemdesignlab.urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.dto.RedirectEvent;

@Service
public class KafkaProducerService {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaProducerService.class);

    private static final String TOPIC = "redirect-events";

    private final KafkaTemplate<String, RedirectEvent> kafkaTemplate;

    public KafkaProducerService(
            KafkaTemplate<String, RedirectEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(RedirectEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getShortCode(),   // Kafka Key
                event);

        log.info("Published redirect event for {}",
                event.getShortCode());
    }
}
package com.systemdesignlab.urlshortener.service;

import com.systemdesignlab.urlshortener.dto.RedirectEvent;
import com.systemdesignlab.urlshortener.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaConsumerService.class);

    private final UrlRepository repository;

    public KafkaConsumerService(
            UrlRepository repository) {

        this.repository = repository;
    }

    @KafkaListener(
            topics = "redirect-events",
            groupId = "analytics-group")
    public void consume(RedirectEvent event) {

        log.info(
                "Received redirect event {}",
                event.getShortCode());

        repository.incrementClickCount(
                event.getShortCode());

        log.info(
                "Click count updated for {}",
                event.getShortCode());
    }
}
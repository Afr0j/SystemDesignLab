package com.systemdesignlab.urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.dto.RedirectEvent;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class KafkaProducerService {

	private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
	private final Counter publishedCounter;

	private static final String TOPIC = "redirect-events";

	private final KafkaTemplate<String, RedirectEvent> kafkaTemplate;

	public KafkaProducerService(KafkaTemplate<String, RedirectEvent> kafkaTemplate, MeterRegistry meterRegistry) {

		this.kafkaTemplate = kafkaTemplate;
		this.publishedCounter = meterRegistry.counter("kafka.events.published");

	}

	public void publish(RedirectEvent event) {

		kafkaTemplate.send(TOPIC, event.getShortCode(), event);
		publishedCounter.increment();

		log.info("Published redirect event for {}", event.getShortCode());

	}

}
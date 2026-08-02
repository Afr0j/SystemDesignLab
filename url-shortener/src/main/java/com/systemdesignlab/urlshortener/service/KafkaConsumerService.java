package com.systemdesignlab.urlshortener.service;

import com.systemdesignlab.urlshortener.dto.RedirectEvent;
import com.systemdesignlab.urlshortener.entity.AnalyticsEvent;
import com.systemdesignlab.urlshortener.entity.ProcessedEvent;
import com.systemdesignlab.urlshortener.repository.AnalyticsRepository;
import com.systemdesignlab.urlshortener.repository.ProcessedEventRepository;
import com.systemdesignlab.urlshortener.repository.UrlRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaConsumerService.class);

    private final UrlRepository repository;
    private final AnalyticsRepository analyticsRepository; 
    private final ProcessedEventRepository processedEventRepository;

    public KafkaConsumerService(
            UrlRepository repository,
            AnalyticsRepository analyticsRepository,
            ProcessedEventRepository processedEventRepository) {

        this.repository = repository;
        this.analyticsRepository = analyticsRepository;
        this.processedEventRepository=processedEventRepository;
    }
    
    @Transactional
    @KafkaListener(
            topics = "redirect-events",
            groupId = "analytics-group")
    public void consume(RedirectEvent event) {
//    	throw new RuntimeException("Testing DLQ"); 
    	
    	
    	log.info(
    		    "Received event {} for shortCode={}",
    		    event.getEventId(),
    		    event.getShortCode());
        
        try{
        	

            processedEventRepository.save(

                new ProcessedEvent(

                    event.getEventId()

                )

            );

        }
        catch(DataIntegrityViolationException e){

        	log.info(
        		    "Duplicate event ignored: {}",
        		    event.getEventId()
        		);

            return;

        }

        
        AnalyticsEvent analyticsEvent =
                new AnalyticsEvent();
        

        analyticsEvent.setShortCode(
                event.getShortCode());

        analyticsEvent.setClickedAt(
                event.getTimestamp());

        analyticsEvent.setIpAddress(
                event.getIpAddress());

        analyticsEvent.setUserAgent(
                event.getUserAgent());
        
        analyticsRepository.saveAndFlush(
                analyticsEvent);

        repository.incrementClickCount(
                event.getShortCode());

        log.info(
                "Click count updated for {}",
                event.getShortCode());
    }
}
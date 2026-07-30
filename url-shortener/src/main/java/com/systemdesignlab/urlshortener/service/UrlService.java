package com.systemdesignlab.urlshortener.service;

import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.entity.UrlMapping;
import com.systemdesignlab.urlshortener.exception.UrlNotFoundException;
import com.systemdesignlab.urlshortener.repository.UrlRepository;
import com.systemdesignlab.urlshortener.util.Base62Encoder;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UrlService {
	private final UrlRepository repository;
	private static final Logger log =LoggerFactory.getLogger(UrlService.class);

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }
    
    @Transactional
    public String shorten(String longUrl) {
    	
    	log.info("Creating short URL for {}", longUrl);

    	UrlMapping entity = new UrlMapping();
    	entity.setLongUrl(longUrl);

    	repository.save(entity);

    	log.debug("Generated database id={}", entity.getId());

    	String shortCode = Base62Encoder.encode(entity.getId());

    	entity.setShortCode(shortCode);

    	repository.save(entity);

    	log.info("Created short code {} for {}", shortCode, longUrl);

    	return shortCode;
    }
    
    @Transactional
    public String redirect(String shortCode) {
    	log.info("Redirect requested for {}", shortCode);

    	UrlMapping url = repository.findByShortCode(shortCode)
    	        .orElseThrow(() -> new UrlNotFoundException(shortCode));

    	log.debug("Current click count={}", url.getClickCount());

    	url.setClickCount(url.getClickCount() + 1);

    	repository.save(url);

    	log.info("Redirecting {} to {}", shortCode, url.getLongUrl());

    	return url.getLongUrl();
    }

}
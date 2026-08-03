package com.systemdesignlab.urlshortener.service;

import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.dto.RedirectEvent;
import com.systemdesignlab.urlshortener.entity.UrlMapping;
import com.systemdesignlab.urlshortener.exception.RedisUnavailableException;
import com.systemdesignlab.urlshortener.exception.UrlNotFoundException;
import com.systemdesignlab.urlshortener.repository.UrlRepository;
import com.systemdesignlab.urlshortener.util.Base62Encoder;
import com.systemdesignlab.urlshortener.util.BloomFilter;
import com.systemdesignlab.urlshortener.util.SnowflakeGenerator;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UrlService {
	private final UrlRepository repository;
	private final SnowflakeGenerator snowflakeGenerator;
	private final CacheService cacheService;
	private final KafkaProducerService kafkaProducerService;
	private final BloomFilter bloomFilter;
	
	private static final Logger log =LoggerFactory.getLogger(UrlService.class);

	public UrlService(
	        UrlRepository repository,
	        SnowflakeGenerator snowflakeGenerator,
	        CacheService cacheService,
	        KafkaProducerService kafkaProducerService,
	        BloomFilter bloomFilter) {

	    this.repository = repository;
	    this.snowflakeGenerator = snowflakeGenerator;
	    this.cacheService = cacheService;
	    this.kafkaProducerService = kafkaProducerService;
	    this.bloomFilter=bloomFilter;
	}
    
    @Transactional
    public String shorten(String longUrl) {

    	
    	log.info("Creating short URL for {}", longUrl);

    	long id = snowflakeGenerator.nextId();

    	String shortCode =
    	        Base62Encoder.encode(id);
    	log.info("Generated shortCode={}, length={}", shortCode, shortCode.length());

    	UrlMapping entity = new UrlMapping();

    	entity.setId(id);

    	entity.setLongUrl(longUrl);

    	entity.setShortCode(shortCode);

    	repository.save(entity);
    	bloomFilter.add(shortCode);

    	log.info("Created short code {} for {}", shortCode, longUrl);

    	return shortCode;
    }
    
    @Transactional
    public String redirect(String shortCode, String ipAddress, String userAgent) {
    	if (!bloomFilter.mightContain(shortCode)) {

            log.info(
                "Bloom Filter rejected {}",
                shortCode);

            throw new UrlNotFoundException(shortCode);
        }

        log.info("Redirect requested for {}", shortCode);
        
        

        String cachedUrl = null;

        try {

            cachedUrl =
                    cacheService.getLongUrl(shortCode);

        }
        catch (RedisUnavailableException ex) {

            log.warn(
                "Redis unavailable. Falling back to MySQL.");
        }
        

        if (cachedUrl != null) {

            log.info("Cache HIT for {}", shortCode);

            kafkaProducerService.publish(
            		new RedirectEvent(
            				UUID.randomUUID(),
            		        shortCode,
            		        LocalDateTime.now(),
            		        ipAddress,
            		        userAgent
            		));

            return cachedUrl;
        }

        log.info("Cache MISS for {}", shortCode);

        UrlMapping url = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));

        kafkaProducerService.publish(
        		new RedirectEvent(
        				UUID.randomUUID(),
        		        shortCode,
        		        LocalDateTime.now(),
        		        ipAddress,
        		        userAgent
        		));

        try {

            cacheService.cacheLongUrl(
                    shortCode,
                    url.getLongUrl());

        }
        catch (RedisUnavailableException ex) {

            log.warn(
                    "Redis unavailable. Skipping cache update.");
        }

        log.info("Cached {} -> {}", shortCode, url.getLongUrl());

        return url.getLongUrl();
    }
}
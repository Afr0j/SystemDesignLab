package com.systemdesignlab.urlshortener.service;

import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.entity.UrlMapping;
import com.systemdesignlab.urlshortener.exception.UrlNotFoundException;
import com.systemdesignlab.urlshortener.repository.UrlRepository;
import com.systemdesignlab.urlshortener.util.Base62Encoder;

import jakarta.transaction.Transactional;

@Service
public class UrlService {
	private final UrlRepository repository;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }
    
    @Transactional
    public String shorten(String longUrl) {

        // TODO: Generate Short Code
    	
    	
    	UrlMapping entity = new UrlMapping();
        entity.setLongUrl(longUrl);

        repository.save(entity);

        String shortCode =
                Base62Encoder.encode(entity.getId());

        entity.setShortCode(shortCode);

        repository.save(entity);

        return shortCode;

    }
    
    @Transactional
    public String redirect(String shortCode) {
    	UrlMapping url = repository.findByShortCode(shortCode)
    	        .orElseThrow(() -> new UrlNotFoundException(shortCode));

    	url.setClickCount(url.getClickCount() + 1);

    	repository.save(url);

    	return url.getLongUrl();
    }

}
package com.systemdesignlab.urlshortener.service;

import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.entity.UrlMapping;
import com.systemdesignlab.urlshortener.repository.UrlRepository;
import com.systemdesignlab.urlshortener.util.Base62Encoder;

@Service
public class UrlService {
	private final UrlRepository repository;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

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

}
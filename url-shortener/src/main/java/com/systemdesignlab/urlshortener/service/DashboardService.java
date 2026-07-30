package com.systemdesignlab.urlshortener.service;


import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.repository.UrlRepository;

@Service
public class DashboardService {
	private final UrlRepository repository;

    public DashboardService(UrlRepository repository) {
        this.repository = repository;
    }
    
    public long totalUrls() {
    	return repository.count();
    }
    
    public long totalRedirects() {
    	return repository.getTotalRedirects();
    }
    
    public double averageClicks() {
    	return repository.getAverageClicks();
    }
    
    public String mostClickedUrl() {
    	return repository.findTopByOrderByClickCountDesc().get().getLongUrl();
    }

}

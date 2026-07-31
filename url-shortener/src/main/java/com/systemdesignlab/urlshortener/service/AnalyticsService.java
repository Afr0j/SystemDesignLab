package com.systemdesignlab.urlshortener.service;

import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.dto.AnalyticsResponse;
import com.systemdesignlab.urlshortener.entity.AnalyticsEvent;
import com.systemdesignlab.urlshortener.entity.UrlMapping;
import com.systemdesignlab.urlshortener.exception.UrlNotFoundException;
import com.systemdesignlab.urlshortener.repository.AnalyticsRepository;
import com.systemdesignlab.urlshortener.repository.UrlRepository;

@Service
public class AnalyticsService {

    private final UrlRepository urlRepository;
    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(
            UrlRepository urlRepository,
            AnalyticsRepository analyticsRepository) {

        this.urlRepository = urlRepository;
        this.analyticsRepository = analyticsRepository;
    }

    public AnalyticsResponse getAnalytics(String shortCode) {

        UrlMapping url = urlRepository
                .findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException(shortCode));

        AnalyticsEvent latestEvent =
                analyticsRepository
                        .findTopByShortCodeOrderByClickedAtDesc(shortCode);

        return new AnalyticsResponse(
                shortCode,
                url.getClickCount(),
                latestEvent == null
                        ? null
                        : latestEvent.getClickedAt());
    }
}
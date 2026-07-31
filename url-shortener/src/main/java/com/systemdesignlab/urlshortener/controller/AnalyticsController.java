package com.systemdesignlab.urlshortener.controller;

import com.systemdesignlab.urlshortener.dto.AnalyticsResponse;
import com.systemdesignlab.urlshortener.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<AnalyticsResponse> getAnalytics(
            @PathVariable String shortCode) {

        return ResponseEntity.ok(
                analyticsService.getAnalytics(shortCode));
    }
}
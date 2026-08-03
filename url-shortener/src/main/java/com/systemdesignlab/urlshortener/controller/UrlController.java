package com.systemdesignlab.urlshortener.controller;

import com.systemdesignlab.urlshortener.dto.ShortenUrlRequest;
import com.systemdesignlab.urlshortener.dto.ShortenUrlResponse;
import com.systemdesignlab.urlshortener.exception.RedisUnavailableException;
import com.systemdesignlab.urlshortener.service.RateLimiterService;
import com.systemdesignlab.urlshortener.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlService urlService;
    private final RateLimiterService rateLimiterService;
    private static final Logger log =
            LoggerFactory.getLogger(UrlController.class);

    public UrlController(UrlService urlService, RateLimiterService rateLimiterService) {
        this.urlService = urlService;
        this.rateLimiterService=rateLimiterService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shorten(
            @Valid @RequestBody ShortenUrlRequest request) {

        String shortCode = urlService.shorten(request.getUrl());

        return ResponseEntity.ok(
                new ShortenUrlResponse(shortCode)
        );
    }
    
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();

        String userAgent =
                request.getHeader("User-Agent");

        try {

            if (!rateLimiterService.allowRequest(ip)) {

                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .build();
            }

        } catch (RedisUnavailableException ex) {

            log.warn(
                    "Rate Limiter unavailable. Allowing request.");

        }

        String longUrl =
                urlService.redirect(
                        shortCode,
                        ip,
                        userAgent);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}
package com.systemdesignlab.urlshortener.controller;

import com.systemdesignlab.urlshortener.dto.ShortenUrlRequest;
import com.systemdesignlab.urlshortener.dto.ShortenUrlResponse;
import com.systemdesignlab.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shorten(
            @Valid @RequestBody ShortenUrlRequest request) {

        String shortCode = urlService.shorten(request.getUrl());

        return ResponseEntity.ok(
                new ShortenUrlResponse(shortCode)
        );
    }
}
package com.systemdesignlab.urlshortener.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyticsResponse {

    private String shortCode;

    private long totalClicks;

    private LocalDateTime lastClickedAt;
}
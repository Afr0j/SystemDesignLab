package com.systemdesignlab.urlshortener.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedirectEvent {

	private String shortCode;

	private LocalDateTime timestamp;

	private String ipAddress;

	private String userAgent;
}
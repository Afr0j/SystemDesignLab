package com.systemdesignlab.urlshortener.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedirectEvent {
	private UUID eventId;

	private String shortCode;

	private LocalDateTime timestamp;

	private String ipAddress;

	private String userAgent;
}
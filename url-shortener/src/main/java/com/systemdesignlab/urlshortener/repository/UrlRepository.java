package com.systemdesignlab.urlshortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.systemdesignlab.urlshortener.entity.UrlMapping;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
	
	Optional<UrlMapping> findByShortCode(String shortCode);
}

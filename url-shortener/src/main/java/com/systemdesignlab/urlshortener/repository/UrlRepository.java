package com.systemdesignlab.urlshortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.systemdesignlab.urlshortener.entity.UrlMapping;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
	
	Optional<UrlMapping> findByShortCode(String shortCode);
	
	@Query("SELECT COALESCE(SUM(u.clickCount),0) FROM UrlMapping u")
	Long getTotalRedirects();
	
	@Query("SELECT COALESCE(AVG(u.clickCount),0) FROM UrlMapping u")
	Double getAverageClicks();
	
	Optional<UrlMapping> findTopByOrderByClickCountDesc();
	
}

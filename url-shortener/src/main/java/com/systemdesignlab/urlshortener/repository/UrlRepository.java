package com.systemdesignlab.urlshortener.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.systemdesignlab.urlshortener.entity.UrlMapping;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
	
	Optional<UrlMapping> findByShortCode(String shortCode);
	
	@Query("SELECT COALESCE(SUM(u.clickCount),0) FROM UrlMapping u")
	Long getTotalRedirects();
	
	@Query("SELECT COALESCE(AVG(u.clickCount),0) FROM UrlMapping u")
	Double getAverageClicks();
	
	Optional<UrlMapping> findTopByOrderByClickCountDesc();
	
	@Transactional
	@Modifying
	@Query("""
	UPDATE UrlMapping u
	SET u.clickCount = u.clickCount + 1
	WHERE u.shortCode = :shortCode
	""")
	void incrementClickCount(
	        @Param("shortCode")
	        String shortCode);
	
	@Query("""
			SELECT u.shortCode
			FROM UrlMapping u
			""")
			List<String> findAllShortCodes();
	
}

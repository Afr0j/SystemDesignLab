package com.systemdesignlab.urlshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.systemdesignlab.urlshortener.entity.UrlMapping;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
}

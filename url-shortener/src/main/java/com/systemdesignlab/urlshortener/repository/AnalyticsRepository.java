package com.systemdesignlab.urlshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.systemdesignlab.urlshortener.entity.AnalyticsEvent;
@Repository
public interface AnalyticsRepository
        extends JpaRepository<AnalyticsEvent, Long> {

}
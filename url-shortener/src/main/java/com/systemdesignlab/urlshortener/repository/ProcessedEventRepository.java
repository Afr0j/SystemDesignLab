package com.systemdesignlab.urlshortener.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.systemdesignlab.urlshortener.entity.ProcessedEvent;

@Repository
public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, UUID> {

}

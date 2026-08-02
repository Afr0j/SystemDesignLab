package com.systemdesignlab.urlshortener.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.systemdesignlab.urlshortener.repository.UrlRepository;
import com.systemdesignlab.urlshortener.util.BloomFilter;

@Component
public class BloomFilterInitializer
        implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(
                    BloomFilterInitializer.class);

    private final UrlRepository repository;
    private final BloomFilter bloomFilter;

    public BloomFilterInitializer(
            UrlRepository repository,
            BloomFilter bloomFilter) {

        this.repository = repository;
        this.bloomFilter = bloomFilter;
    }

    @Override
    public void run(String... args) {

        List<String> shortCodes =
                repository.findAllShortCodes();

        for (String shortCode : shortCodes) {
            bloomFilter.add(shortCode);
        }


        log.info(
                "Loaded {} short codes into Bloom Filter",
                shortCodes.size());
    }
}
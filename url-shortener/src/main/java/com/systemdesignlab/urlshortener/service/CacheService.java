package com.systemdesignlab.urlshortener.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private static final String URL_PREFIX = "url:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    private final RedisTemplate<String, String> redisTemplate;

    public CacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheLongUrl(String shortCode, String longUrl) {

        redisTemplate
                .opsForValue()
                .set(URL_PREFIX + shortCode,
                     longUrl,
                     CACHE_TTL);
    }

    public String getLongUrl(String shortCode) {

        return redisTemplate
                .opsForValue()
                .get(URL_PREFIX + shortCode);
    }

    public void evict(String shortCode) {

        redisTemplate.delete(URL_PREFIX + shortCode);
    }
}
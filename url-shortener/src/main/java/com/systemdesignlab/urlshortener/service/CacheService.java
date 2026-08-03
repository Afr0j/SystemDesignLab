package com.systemdesignlab.urlshortener.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.exception.RedisUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class CacheService {

	private static final String URL_PREFIX = "url:";
	private static final Duration CACHE_TTL = Duration.ofHours(24);
	private final Counter cacheHitCounter;
	private final Counter cacheMissCounter;
	private final Counter cacheFailureCounter;

	private final RedisTemplate<String, String> redisTemplate;

	public CacheService(RedisTemplate<String, String> redisTemplate, MeterRegistry meterRegistry) {
		this.redisTemplate = redisTemplate;
		this.cacheHitCounter = meterRegistry.counter("cache.hit");
		this.cacheMissCounter = meterRegistry.counter("cache.miss");
		this.cacheFailureCounter = meterRegistry.counter("cache.redis.failure");
	}

	@CircuitBreaker(name = "redisCache", fallbackMethod = "redisUnavailable")
	public String getLongUrl(String shortCode) {

		String value = redisTemplate.opsForValue().get(URL_PREFIX + shortCode);
		if (value == null) {

			cacheMissCounter.increment();

		} else {

			cacheHitCounter.increment();

		}

		return value;
	}

	@CircuitBreaker(name = "redisCache", fallbackMethod = "cacheUnavailable")
	public void cacheLongUrl(String shortCode, String longUrl) {

		redisTemplate.opsForValue().set(URL_PREFIX + shortCode, longUrl, CACHE_TTL);
	}

	@CircuitBreaker(name = "redisCache", fallbackMethod = "evictUnavailable")
	public void evict(String shortCode) {

		redisTemplate.delete(URL_PREFIX + shortCode);
	}

	public String redisUnavailable(String shortCode, Throwable ex) {
		cacheFailureCounter.increment();

		throw new RedisUnavailableException(ex);
	}

	public void cacheUnavailable(String shortCode, String longUrl, Throwable ex) {
		cacheFailureCounter.increment();

		throw new RedisUnavailableException(ex);
	}

	public void evictUnavailable(String shortCode, Throwable ex) {
		cacheFailureCounter.increment();

		throw new RedisUnavailableException(ex);
	}
}
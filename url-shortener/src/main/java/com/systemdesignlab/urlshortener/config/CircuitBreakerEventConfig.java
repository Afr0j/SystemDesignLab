package com.systemdesignlab.urlshortener.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Configuration
public class CircuitBreakerEventConfig {

    private static final Logger log =
            LoggerFactory.getLogger(
                    CircuitBreakerEventConfig.class);

    @Bean
    public ApplicationRunner registerListeners(
            CircuitBreakerRegistry registry) {

        return args -> {

            registry.getAllCircuitBreakers()
                    .forEach(cb ->

                        cb.getEventPublisher()
                                .onStateTransition(event ->

                                        log.warn(
                                                "Circuit [{}] : {}",
                                                cb.getName(),
                                                event.getStateTransition())
                                )
                    );
        };
    }
}
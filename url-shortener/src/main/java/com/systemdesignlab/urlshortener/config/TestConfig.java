package com.systemdesignlab.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class TestConfig {

    @Bean
    public String test(KafkaTemplate<?, ?> template) {
        return "OK";
    }

}
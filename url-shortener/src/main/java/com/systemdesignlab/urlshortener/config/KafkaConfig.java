package com.systemdesignlab.urlshortener.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.backoff.FixedBackOff;

import com.systemdesignlab.urlshortener.dto.RedirectEvent;

@Configuration
public class KafkaConfig {
	@Bean
	public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
	        KafkaTemplate<String, Object> kafkaTemplate) {

	    return new DeadLetterPublishingRecoverer(
	            kafkaTemplate,
	            (record, exception) ->
	                    new TopicPartition(
	                            record.topic() + "-dlt",
	                            record.partition()
	                    )
	    );
	}
	@Bean
	public DefaultErrorHandler errorHandler(
	        DeadLetterPublishingRecoverer recoverer) {

	    FixedBackOff backOff =
	            new FixedBackOff(3000L, 3);

	    DefaultErrorHandler handler =
	            new DefaultErrorHandler(
	                    recoverer,
	                    backOff);

	    handler.addNotRetryableExceptions(
	            IllegalArgumentException.class,
	            NullPointerException.class,
	            DeserializationException.class,
	            MessageConversionException.class
	            );

	    return handler;
	}
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, RedirectEvent>
	kafkaListenerContainerFactory(
	        ConsumerFactory<String, RedirectEvent> consumerFactory,
	        DefaultErrorHandler errorHandler) {

	    ConcurrentKafkaListenerContainerFactory<String, RedirectEvent> factory =
	            new ConcurrentKafkaListenerContainerFactory<>();

	    factory.setConsumerFactory(consumerFactory);

	    factory.setCommonErrorHandler(errorHandler);

	    return factory;
	}

}

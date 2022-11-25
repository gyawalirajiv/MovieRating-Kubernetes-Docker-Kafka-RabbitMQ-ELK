package edu.miu.movieservice.config;

import edu.miu.movieservice.entities.DTOs.AvgRatingDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AvgRatingDto> kafkaListenerContainerFactory(ConsumerFactory<String, AvgRatingDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, AvgRatingDto> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }

    @Bean
    ConsumerFactory<String, AvgRatingDto> consumerFactory(KafkaProperties kafkaProperties) {
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();

        Map<String, Class<?>> classMap = new HashMap<>();
        // any name key you want
        classMap.put("miu.edu.AvgRatingDto", AvgRatingDto.class);
        typeMapper.setIdClassMapping(classMap);

        typeMapper.addTrustedPackages("*");

        JsonDeserializer<AvgRatingDto> valueDeserializer = new JsonDeserializer<>(AvgRatingDto.class);
        valueDeserializer.setTypeMapper(typeMapper);
        valueDeserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(),
                new StringDeserializer(),
                valueDeserializer);
    }
}

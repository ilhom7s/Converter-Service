package com.file.converter.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic convertFiles(
            @Value("${app.kafka.topics.convert-requests}")String name){
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(3)
                .build();
    }
    @Bean
    public NewTopic convertSuccessTopic(
            @Value("${app.kafka.convert-success-result}") String name){
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(3)
                .build();
    }

}

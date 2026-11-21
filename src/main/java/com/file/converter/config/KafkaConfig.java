package com.file.converter.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaConfig {
    @Value("${app.kafka.topics.convert-requests}")
    private String convertFileTopicName;

    @Value("${app.kafka.convert-success-result}")
    private String convertSuccessTopicName;

    @Bean
    public NewTopic convertFiles(){
        return TopicBuilder.name(convertFileTopicName)
                .partitions(3)
                .replicas(3)
                .build();
    }
    @Bean
    public NewTopic convertSuccessTopic(){
        return TopicBuilder.name(convertSuccessTopicName)
                .partitions(3)
                .replicas(3)
                .build();
    }

}

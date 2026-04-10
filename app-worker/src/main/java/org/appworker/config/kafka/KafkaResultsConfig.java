package org.appworker.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaResultsConfig {

    @Bean
    public NewTopic eventsTopic() {
        return TopicBuilder.name("match-events").partitions(1).replicas(1).build();

    }
}

package fr.checkconsulting.scpiinvapi.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    private final TopicNameProvider topicNameProvider;

    public KafkaTopicConfig(TopicNameProvider topicNameProvider) {
        this.topicNameProvider = topicNameProvider;
    }

    @Bean
    public NewTopic documentValidationTopic() {
        return TopicBuilder.name(topicNameProvider.getDocumentValidationTopic())
                .partitions(1)
                .replicas(1)
                .build();
    }

}

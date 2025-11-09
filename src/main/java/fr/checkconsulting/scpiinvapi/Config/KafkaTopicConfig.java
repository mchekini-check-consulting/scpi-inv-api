package fr.checkconsulting.scpiinvapi.config;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Collections;
import java.util.Map;

@Configuration
@Profile("!test")
public class KafkaTopicConfig {

    private final KafkaAdmin kafkaAdmin;
    private final TopicNameProvider topicNameProvider;

    public KafkaTopicConfig(KafkaAdmin kafkaAdmin, TopicNameProvider topicNameProvider) {
        this.kafkaAdmin = kafkaAdmin;
        this.topicNameProvider = topicNameProvider;
    }

    @PostConstruct
    public void setupTopic() throws Exception {
        String topicName = topicNameProvider.getDocumentValidationTopic();
        NewTopic topic = TopicBuilder.name(topicName)
                .partitions(1)
                .replicas(1)
                .build();

        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            Map<String, TopicListing> topics = admin.listTopics().namesToListings().get();
            if (!topics.containsKey(topicName)) {
                admin.createTopics(Collections.singletonList(topic)).all().get();
            }
        }
    }
}

package fr.checkconsulting.scpiinvapi.config;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Collections;
import java.util.Set;

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
    public void setupTopic() {
        String topicName = topicNameProvider.getDocumentValidationTopic();
        NewTopic topic = TopicBuilder.name(topicName)
                .partitions(1)
                .replicas(1)
                .build();

        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            Set<String> existingTopics = admin.listTopics().names().get();

            if (!existingTopics.contains(topicName)) {
                try {
                    admin.createTopics(Collections.singletonList(topic)).all().get();
                } catch (Exception e) {
                    if (!(e.getCause() instanceof TopicExistsException)) {
                        throw e;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}


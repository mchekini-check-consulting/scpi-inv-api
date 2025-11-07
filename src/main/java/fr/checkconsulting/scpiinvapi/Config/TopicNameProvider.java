package fr.checkconsulting.scpiinvapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static fr.checkconsulting.scpiinvapi.utils.Constants.DOCUMENT_VALIDATION_TOPIC;

@Component
public class TopicNameProvider {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    public String getDocumentValidationTopic() {
        return DOCUMENT_VALIDATION_TOPIC + activeProfile;
    }
}

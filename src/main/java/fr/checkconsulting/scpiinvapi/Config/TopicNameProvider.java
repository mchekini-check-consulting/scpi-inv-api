package fr.checkconsulting.scpiinvapi.config;

import org.springframework.stereotype.Component;

import static fr.checkconsulting.scpiinvapi.utils.Constants.DOCUMENT_VALIDATION_TOPIC;

@Component
public class TopicNameProvider {
    public String getDocumentValidationTopic() {
        return DOCUMENT_VALIDATION_TOPIC;
    }

}

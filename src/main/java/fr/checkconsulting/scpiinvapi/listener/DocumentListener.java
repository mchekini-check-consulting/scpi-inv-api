package fr.checkconsulting.scpiinvapi.listener;

import fr.checkconsulting.scpiinvapi.config.TopicNameProvider;
import fr.checkconsulting.scpiinvapi.dto.request.UserDocumentDto;
import fr.checkconsulting.scpiinvapi.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentListener {

    private static final Logger log = LoggerFactory.getLogger(DocumentListener.class);

    private final DocumentService documentService;
    private final TopicNameProvider topicNameProvider;

    public DocumentListener(DocumentService documentService, TopicNameProvider topicNameProvider) {
        this.documentService = documentService;
        this.topicNameProvider = topicNameProvider;
    }

    @KafkaListener(
            topics = "document-validation-response-topic-${spring.profiles.active:local}",
            groupId = "scpi-inv-api-group-${spring.profiles.active:local}"
    )
    public void consume(UserDocumentDto userDocumentDto) {
        log.info(
                "Réponse reçue depuis Kafka sur le topic [{}] : {}",
                topicNameProvider.getDocumentValidationResponseTopic(),
                userDocumentDto
        );
        documentService.updateStatus(userDocumentDto);
    }


}

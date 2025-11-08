package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.config.TopicNameProvider;
import fr.checkconsulting.scpiinvapi.dto.request.UserDocumentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicNameProvider topicNameProvider;

    public void sendDocumentEvent(UserDocumentDto documentDto) {
        String topic = topicNameProvider.getDocumentValidationTopic();
        log.info(" Envoi du message Kafka sur le topic [{}] pour l'utilisateur [{}], document [{}]",
                topic, documentDto.getUserEmail(), documentDto.getStoredFileName());

        kafkaTemplate.send(topic, documentDto);
    }

}
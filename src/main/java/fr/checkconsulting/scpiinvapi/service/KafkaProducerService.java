package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.request.UserDocumentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static fr.checkconsulting.scpiinvapi.utils.Constants.DOCUMENT_VALIDATION_TOPIC;
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void sendDocumentEvent(UserDocumentDto documentDto) {

        log.info(" Envoi du message Kafka pour l'utilisateur [{}], document [{}]",
                documentDto.getUserEmail(), documentDto.getStoredFileName());

        kafkaTemplate.send(DOCUMENT_VALIDATION_TOPIC, documentDto);

    }
}

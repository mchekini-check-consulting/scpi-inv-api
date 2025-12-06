package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.request.UserDocumentDto;
import fr.checkconsulting.scpiinvapi.dto.response.DocumentStatusResponse;
import fr.checkconsulting.scpiinvapi.mapper.UserDocumentMapper;
import fr.checkconsulting.scpiinvapi.model.entity.UserDocument;
import fr.checkconsulting.scpiinvapi.model.enums.DocumentStatus;
import fr.checkconsulting.scpiinvapi.model.enums.DocumentType;
import fr.checkconsulting.scpiinvapi.repository.UserDocumentRepository;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class MinioService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final MinioClient minioClient;
    private final UserService userService;
    private final UserDocumentRepository userDocumentRepository;
    private final UserDocumentMapper userDocumentMapper;
    private final KafkaProducerService kafkaProducerService;

    public MinioService(MinioClient minioClient,
                        UserService userService,
                        UserDocumentRepository userDocumentRepository,
                        UserDocumentMapper userDocumentMapper,
                        KafkaProducerService kafkaProducerService) {
        this.minioClient = minioClient;
        this.userService = userService;
        this.userDocumentRepository = userDocumentRepository;
        this.userDocumentMapper = userDocumentMapper;
        this.kafkaProducerService = kafkaProducerService;
    }

    public String uploadFile(MultipartFile file, String bucketName) {
        String userId = userService.getUserId();
        String userEmail = userService.getEmail();
        String fullName = userService.getFullName();
        String documentId = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename();

        log.info("Upload fichier={} pour user={} dans bucket={}", fileName, userEmail, bucketName);

        DocumentType type = resolveDocumentType(bucketName);

        try (InputStream inputStream = file.getInputStream()) {
            putObject(bucketName, userId + "/" + fileName, inputStream, file.getSize(), file.getContentType());

            UserDocument entity = userDocumentRepository.findByUserEmailAndType(userEmail, type)
                    .orElseGet(() -> UserDocument.builder()
                            .id(documentId)
                            .userEmail(userEmail)
                            .fullName(fullName)
                            .type(type)
                            .build());

            updateDocumentEntity(entity, fileName, bucketName, file.getOriginalFilename());

            UserDocument saved = userDocumentRepository.save(entity);
            UserDocumentDto documentDto = userDocumentMapper.toDto(saved);

            kafkaProducerService.sendDocumentEvent(documentDto);
            log.info("Document [{}] uploadé et envoyé à Kafka.", documentDto.getStoredFileName());

            return fileName;
        } catch (Exception e) {
            log.error("Erreur lors de l’upload du fichier {}", fileName, e);
            throw new IllegalStateException("Erreur lors de l’upload du fichier : " + e.getMessage(), e);
        }
    }

    public void uploadFile(byte[] data, String bucketName, String fileName, String contentType) {
        log.info("Upload flux binaire fichier={} dans bucket={}", fileName, bucketName);
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            putObject(bucketName, fileName, inputStream, data.length, contentType);
        } catch (Exception e) {
            log.error("Erreur lors de l’upload du flux binaire {}", fileName, e);
            throw new IllegalStateException("Erreur lors de l’upload du flux binaire " + fileName, e);
        }
    }

    public InputStream downloadFile(String bucketName, String fileName) {
        log.info("Téléchargement fichier={} depuis bucket={}", fileName, bucketName);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erreur lors du téléchargement du fichier {}", fileName, e);
            throw new IllegalStateException("Erreur lors du téléchargement du fichier : " + fileName, e);
        }
    }

    public byte[] downloadFileAsBytes(String fileName, String bucketName) {
        log.info("Lecture fichier={} en bytes depuis bucket={}", fileName, bucketName);
        try (InputStream stream = downloadFile(bucketName, fileName)) {
            return stream.readAllBytes();
        } catch (Exception e) {
            log.error("Erreur lors de la lecture du fichier {}", fileName, e);
            throw new IllegalStateException("Erreur lors de la lecture du fichier " + fileName, e);
        }
    }

    public void deleteFile(String fileName, String bucketName) {
        log.info("Suppression fichier={} depuis bucket={}", fileName, bucketName);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du fichier {}", fileName, e);
            throw new IllegalStateException("Erreur lors de la suppression du fichier " + fileName, e);
        }
    }

    public StatObjectResponse getFileMetadata(String fileName, String bucketName) {
        log.info("Récupération des métadonnées fichier={} depuis bucket={}", fileName, bucketName);
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Fichier non trouvé ou inaccessible {}", fileName, e);
            throw new IllegalStateException("Fichier non trouvé ou inaccessible : " + fileName, e);
        }
    }

    public DocumentStatusResponse getDocumentStatus() {
        String userEmail = userService.getEmail();
        log.info("Vérification du statut des documents pour user={}", userEmail);

        List<UserDocument> documents = userDocumentRepository.findByUserEmail(userEmail);

        boolean allUploaded = Arrays.stream(DocumentType.values())
                .allMatch(type -> documents.stream()
                        .anyMatch(doc -> doc.getType() == type && doc.getStatus() == DocumentStatus.UPLOADED));

        log.debug("Statut documents pour user={} : allUploaded={}", userEmail, allUploaded);
        return new DocumentStatusResponse(userEmail, allUploaded);
    }

    private void putObject(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, size, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    private DocumentType resolveDocumentType(String bucketName) {
        return Arrays.stream(DocumentType.values())
                .filter(t -> bucketName.endsWith(t.getDocumentType()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Type de document inconnu pour le bucket {}", bucketName);
                    return new IllegalArgumentException("Type de document inconnu pour le bucket : " + bucketName);
                });
    }

    private void updateDocumentEntity(UserDocument entity, String fileName, String bucketName, String originalFileName) {
        entity.setStatus(DocumentStatus.UPLOADED);
        entity.setOriginalFileName(originalFileName);
        entity.setStoredFileName(fileName);
        entity.setBucketName(bucketName);
        entity.setUploadedAt(LocalDateTime.now());
        entity.setLastUpdatedAt(LocalDateTime.now());
    }
}
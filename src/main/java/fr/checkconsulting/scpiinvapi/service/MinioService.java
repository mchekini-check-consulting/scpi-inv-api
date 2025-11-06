package fr.checkconsulting.scpiinvapi.service;

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

@Service
@Slf4j
public class MinioService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final MinioClient minioClient;
    private final UserService userService;
    private final UserDocumentRepository userDocumentRepository;
    private final UserDocumentMapper userDocumentMapper;

    public MinioService(MinioClient minioClient, UserService userService, UserDocumentRepository userDocumentRepository, UserDocumentMapper userDocumentMapper) {
        this.minioClient = minioClient;
        this.userService = userService;
        this.userDocumentRepository = userDocumentRepository;
        this.userDocumentMapper = userDocumentMapper;
    }

    public String uploadFile(MultipartFile file, String bucketName) {
        String userId = userService.getUserId();
        String userEmail = userService.getEmail();
        String fullName = userService.getFullName();

        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        DocumentType type = Arrays.stream(DocumentType.values())
                .filter(t -> bucketName.endsWith(t.getDocumentType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Type de document inconnu pour le bucket : " + bucketName));

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(userId + "/" + fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            Optional<UserDocument> existingOpt = userDocumentRepository.findByUserEmailAndType(userEmail, type);

            if (existingOpt.isPresent()) {
                UserDocument existing = existingOpt.get();
                userDocumentMapper.updateUploadedFields(existing,
                        file.getOriginalFilename(),
                        fileName,
                        bucketName);
                existing.setLastUpdatedAt(LocalDateTime.now());
                userDocumentRepository.save(existing);
            } else {
                userDocumentRepository.save(UserDocument.builder()
                        .userEmail(userEmail)
                        .fullName(fullName)
                        .type(type)
                        .status(DocumentStatus.UPLOADED)
                        .originalFileName(file.getOriginalFilename())
                        .storedFileName(fileName)
                        .bucketName(bucketName)
                        .uploadedAt(LocalDateTime.now())
                        .lastUpdatedAt(LocalDateTime.now())
                        .build());
            }

            return fileName;

        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de l’upload du fichier : " + e.getMessage(), e);
        }
    }

    public void uploadFile(byte[] data, String bucketName, String fileName, String contentType) {
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, data.length, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de l’upload du flux binaire " + fileName, e);
        }
    }

    public InputStream downloadFile(String bucketName, String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors du téléchargement du fichier : " + fileName, e);
        }
    }

    public byte[] downloadFileAsBytes(String fileName, String bucketName) {
        try (InputStream stream = downloadFile(bucketName, fileName)) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la lecture du fichier " + fileName, e);
        }
    }

    public void deleteFile(String fileName, String bucketName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la suppression du fichier " + fileName, e);
        }
    }

    public StatObjectResponse getFileMetadata(String fileName, String bucketName) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Fichier non trouvé ou inaccessible : " + fileName, e);
        }
    }

    public DocumentStatusResponse getDocumentStatus() {
        String userEmail = userService.getEmail();
        List<UserDocument> documents = userDocumentRepository.findByUserEmail(userEmail);

        boolean allUploaded = Arrays.stream(DocumentType.values())
                .allMatch(type -> documents.stream()
                        .anyMatch(doc -> doc.getType() == type && doc.getStatus() == DocumentStatus.UPLOADED));

        return new DocumentStatusResponse(userEmail, allUploaded);
    }

}
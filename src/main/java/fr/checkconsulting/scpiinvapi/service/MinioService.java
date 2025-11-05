package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.DocumentStatusResponse;
import fr.checkconsulting.scpiinvapi.exception.DocumentAlreadyUploadedException;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class MinioService {

    private static final String DOCUMENTS_BUCKET = "documents";

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final MinioClient minioClient;
    private final UserService userService;

    public MinioService(MinioClient minioClient, UserService userService) {
        this.minioClient = minioClient;
        this.userService = userService;
    }

    public String uploadFile(MultipartFile file, String bucketName) {
        String userId = userService.getUserId();
        String fileName = file.getOriginalFilename();

        if (hasUserUploadedDocuments(bucketName)) {
            userService.setDocumentsUploaded(true);
            throw new DocumentAlreadyUploadedException(
                    "L'utilisateur " + userId + " a déjà envoyé ses documents."
            );
        }

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(userId + "/" + fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info(" Fichier '{}' uploadé dans le bucket '{}' pour l’utilisateur '{}'", fileName, bucketName, userId);
            return fileName;
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de l’upload du fichier " + fileName, e);
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
            log.info(" Fichier '{}' supprimé du bucket '{}'", fileName, bucketName);
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

    public boolean hasUserUploadedDocuments(String bucketName) {
        String userId = userService.getUserId();
        try {
            return StreamSupport.stream(
                    minioClient.listObjects(
                            ListObjectsArgs.builder()
                                    .bucket(bucketName)
                                    .prefix(userId + "/")
                                    .recursive(true)
                                    .build()
                    ).spliterator(),
                    false
            ).findAny().isPresent();
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la vérification des documents pour " + userId, e);
        }
    }

    public DocumentStatusResponse getDocumentStatus() {
        boolean uploaded = hasUserUploadedDocuments(DOCUMENTS_BUCKET);
        userService.setDocumentsUploaded(uploaded);

        return new DocumentStatusResponse(
                userService.getUserId(),
                uploaded

        );
    }

}

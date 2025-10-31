package fr.checkconsulting.scpiinvapi.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class MinioService {


    @Value("${spring.profiles.active}")
    String activeProfile;

    private final MinioClient minioClient;
    private final Environment environment;

    public MinioService(MinioClient minioClient, Environment environment) {
        this.minioClient = minioClient;
        this.environment = environment;

    }

    public String uploadFile(MultipartFile file, String bucketName) throws Exception {


        String fileName = file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(activeProfile + "-" + bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        return fileName;
    }

    public InputStream downloadFile(String bucketName, String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    public byte[] downloadFileAsBytes(String fileName, String bucketName) throws Exception {
        try (InputStream stream = downloadFile(bucketName, fileName)) {
            return stream.readAllBytes();
        }
    }

    public void deleteFile(String fileName, String bucketName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    public StatObjectResponse getFileMetadata(String fileName, String bucketName) throws Exception {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new Exception("Fichier non trouvé: " + fileName, e);
        }
    }

    public void uploadFile(byte[] data, String bucketName, String fileName, String contentType) throws Exception {
       

        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(activeProfile + "-" + bucketName)
                            .object(fileName)
                            .stream(inputStream, data.length, -1)
                            .contentType(contentType)
                            .build()
            );
        }

    }

}




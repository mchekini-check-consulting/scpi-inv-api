package fr.checkconsulting.scpiinvapi.batch.reader;

import fr.checkconsulting.scpiinvapi.service.MinioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
@Profile({"int", "qua"})
public class MinioCsvReader implements ReadCsvFile{

    @Value("${spring.profiles.active}")
    private String env;

    private final MinioService minioService;

    public MinioCsvReader(MinioService minioService) {
        this.minioService = minioService;
    }

    @Override
    public InputStreamResource readCsv() throws Exception {
        String bucketName = env + "-data";
        byte[] csvBytes = minioService.downloadFileAsBytes("scpi.csv", bucketName);
        return new InputStreamResource(new ByteArrayInputStream(csvBytes));
    }
}

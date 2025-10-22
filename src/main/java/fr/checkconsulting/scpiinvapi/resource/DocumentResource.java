package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.response.FileBase64Dto;
import fr.checkconsulting.scpiinvapi.model.enums.DocumentType;
import fr.checkconsulting.scpiinvapi.service.MinioService;
import io.minio.StatObjectResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/document")
public class DocumentResource {

    private final MinioService minioService;

    public DocumentResource(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("type") DocumentType type) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (file.isEmpty()) {
                response.put("error", "Le fichier est vide");
                return ResponseEntity.badRequest().body(response);
            }

            String fileName = minioService.uploadFile(file, type.getDocumentType());

            response.put("message", "Fichier uploadé avec succès");
            response.put("fileName", fileName);
            response.put("originalName", file.getOriginalFilename());
            response.put("size", file.getSize());
            response.put("contentType", file.getContentType());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("error", "Erreur lors de l'upload: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<FileBase64Dto> downloadFileBase64(@PathVariable String fileName, @RequestParam("type") DocumentType type) {
        try {

            byte[] fileBytes = minioService.downloadFileAsBytes(fileName, type.getDocumentType());
            String base64Content = Base64.getEncoder().encodeToString(fileBytes);
            StatObjectResponse metadata = minioService.getFileMetadata(fileName, type.getDocumentType());

            FileBase64Dto response = new FileBase64Dto();
            response.setFileName(fileName);
            response.setContentType(metadata.contentType());
            response.setSize(metadata.size());
            response.setBase64Content(base64Content);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}

package fr.checkconsulting.scpiinvapi.dto.request;

import fr.checkconsulting.scpiinvapi.model.enums.DocumentStatus;
import fr.checkconsulting.scpiinvapi.model.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDocumentDto {

    private String id;
    private String userEmail;
    private String fullName;
    private DocumentType type;
    private DocumentStatus status;
    private String originalFileName;
    private String storedFileName;
    private String bucketName;
    private LocalDateTime uploadedAt;
    private LocalDateTime lastUpdatedAt;

}

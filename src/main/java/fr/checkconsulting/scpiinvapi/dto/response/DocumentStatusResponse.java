package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DocumentStatusResponse {

    private String userEmail;
    private boolean documentsUploaded;

}
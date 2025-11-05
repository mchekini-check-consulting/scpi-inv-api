package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentStatusResponse {

    private String userId;
    private boolean documentsUploaded;

}

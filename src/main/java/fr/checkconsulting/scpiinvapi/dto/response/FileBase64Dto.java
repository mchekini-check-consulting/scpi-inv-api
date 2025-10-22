package fr.checkconsulting.scpiinvapi.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileBase64Dto {

    private String fileName;
    private String contentType;
    private Long size;
    private String base64Content;
    private String dataUri;
    private String documentType;
}

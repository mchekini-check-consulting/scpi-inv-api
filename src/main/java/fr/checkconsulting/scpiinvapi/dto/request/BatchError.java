package fr.checkconsulting.scpiinvapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchError {
    private int lineNumber;
    private String type;
    private String message;

    public BatchError(String message) {
    }
}

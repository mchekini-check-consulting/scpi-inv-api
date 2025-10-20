package fr.checkconsulting.scpiinvapi.batch.reports;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsvErrorReport {
    private int lineNumber;
    private String columnName;
    private String message;
}

package fr.checkconsulting.scpiinvapi.exceptions.csvfille;

import lombok.Getter;

@Getter
public class CsvValidationException extends RuntimeException {
    private final String columnName;
    private final int lineNumber;

    public CsvValidationException(String message, String columnName, int lineNumber) {
        super(message);
        this.columnName = columnName;
        this.lineNumber = lineNumber;

    }
}

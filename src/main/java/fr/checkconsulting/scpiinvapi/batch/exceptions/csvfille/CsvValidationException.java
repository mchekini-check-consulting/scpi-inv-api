package fr.checkconsulting.scpiinvapi.batch.exceptions.csvfille;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class CsvValidationException extends RuntimeException {
    private final String columnName;
    private final int lineNumber;

    public CsvValidationException(String message, String columnName, int lineNumber) {
        super(message);
        this.columnName = columnName;
        this.lineNumber = lineNumber;

    }

    public CsvValidationException(String message, Throwable cause) {
        super(message, cause);
        this.columnName = null;
        this.lineNumber = -1;
        log.error("Erreur CSV : {}", message);

    }
}

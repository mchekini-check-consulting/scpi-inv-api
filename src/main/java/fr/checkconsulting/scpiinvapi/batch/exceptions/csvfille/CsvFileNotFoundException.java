package fr.checkconsulting.scpiinvapi.batch.exceptions.csvfille;

public class CsvFileNotFoundException extends RuntimeException {
    public CsvFileNotFoundException(String message) {
        super(message);
    }
}

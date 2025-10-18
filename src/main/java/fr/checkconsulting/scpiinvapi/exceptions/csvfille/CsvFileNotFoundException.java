package fr.checkconsulting.scpiinvapi.exceptions.csvfille;

public class CsvFileNotFoundException extends RuntimeException {
    public CsvFileNotFoundException(String message) {
        super(message);
    }
}

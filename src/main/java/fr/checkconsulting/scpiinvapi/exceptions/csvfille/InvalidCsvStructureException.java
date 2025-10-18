package fr.checkconsulting.scpiinvapi.exceptions.csvfille;

public class InvalidCsvStructureException extends RuntimeException {
    public InvalidCsvStructureException(String message) {
        super(message);
    }
}

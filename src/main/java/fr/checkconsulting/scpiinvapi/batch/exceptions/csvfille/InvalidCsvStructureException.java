package fr.checkconsulting.scpiinvapi.batch.exceptions.csvfille;

public class InvalidCsvStructureException extends RuntimeException {
    public InvalidCsvStructureException(String message) {
        super(message);
    }
}

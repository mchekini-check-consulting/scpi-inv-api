package fr.checkconsulting.scpiinvapi.batch.exceptions.csvfille;

public class EmptyCsvFileException extends RuntimeException {
    public EmptyCsvFileException(String message) {
        super(message);
    }
}

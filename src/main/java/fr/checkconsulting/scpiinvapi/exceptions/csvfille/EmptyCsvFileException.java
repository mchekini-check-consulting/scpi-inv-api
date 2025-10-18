package fr.checkconsulting.scpiinvapi.exceptions.csvfille;

public class EmptyCsvFileException extends RuntimeException {
    public EmptyCsvFileException(String message) {
        super(message);
    }
}

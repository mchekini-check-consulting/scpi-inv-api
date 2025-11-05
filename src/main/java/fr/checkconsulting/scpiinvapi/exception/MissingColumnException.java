package fr.checkconsulting.scpiinvapi.exception;

public class MissingColumnException extends RuntimeException {
    public MissingColumnException(String columnName) {
        super("Colonne obligatoire manquante : " + columnName);
    }
}

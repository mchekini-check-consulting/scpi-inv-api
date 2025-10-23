package fr.checkconsulting.scpiinvapi.batch.exception;

public class MissingColumnException extends RuntimeException {
    public MissingColumnException(String columnName) {
        super("Colonne obligatoire manquante : " + columnName);
    }
}

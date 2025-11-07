package fr.checkconsulting.scpiinvapi.batch.validator;

import fr.checkconsulting.scpiinvapi.exception.MissingColumnException;
import fr.checkconsulting.scpiinvapi.batch.report.BatchErrorCollector;
import fr.checkconsulting.scpiinvapi.model.enums.ScpiField;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CsvValidator {

    private final BatchErrorCollector errorCollector;

    public CsvValidator(BatchErrorCollector errorCollector) {
        this.errorCollector = errorCollector;
    }

    public void validate(FieldSet fieldSet) {
        String demembrementValue = fieldSet.readString(ScpiField.DEMEMBREMENT.getColumnName());
        String decoteDemembrementValue = fieldSet.readString(ScpiField.DECOTE_DEMEMBREMENT.getColumnName());

        for (ScpiField field : ScpiField.values()) {
            try {
                String value = fieldSet.readString(field.getColumnName());

                if (field == ScpiField.DECOTE_DEMEMBREMENT) {
                    if ("Oui".equalsIgnoreCase(demembrementValue) && decoteDemembrementValue.isBlank()) {
                        throwMissingColumn(field.getColumnName(), fieldSet.getValues().length);
                    }
                    continue;
                }

                if (value.isBlank()) {
                    throwMissingColumn(field.getColumnName(), fieldSet.getValues().length);
                }

            } catch (IllegalArgumentException e) {
                throwMissingColumn(field.getColumnName(), fieldSet.getValues().length);
            }
        }

        checkExtraColumns(fieldSet);
    }


    private void checkExtraColumns(FieldSet fieldSet) {
        List<String> extraColumns = Arrays.stream(fieldSet.getNames())
                .filter(name -> Arrays.stream(ScpiField.values())
                        .noneMatch(f -> f.getColumnName().equals(name)))
                .toList();

        extraColumns.forEach(col ->
                addColumnError(0, "COLONNE_SUPPLÉMENTAIRE", "Colonne supplémentaire ignorée : " + col));
    }

    private void throwMissingColumn(String columnName, int lineNumber) {
        addColumnError(lineNumber, "COLONNE_MANQUANTE", "Colonne obligatoire vide ou manquante : " + columnName);
        throw new MissingColumnException(columnName);
    }

    private void addColumnError(int lineNumber, String code, String message) {
        errorCollector.addError(lineNumber, code, message);
    }
}

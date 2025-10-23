package fr.checkconsulting.scpiinvapi.batch.validator;

import fr.checkconsulting.scpiinvapi.batch.exception.MissingColumnException;
import fr.checkconsulting.scpiinvapi.model.enums.ScpiField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.transform.FieldSet;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class CsvValidator {

    public static boolean validate(FieldSet fieldSet) {

        for (ScpiField field : ScpiField.values()) {
            try {
                String value = fieldSet.readString(field.getColumnName());
                if (value == null || value.isBlank()) {
                    log.warn("Colonne obligatoire vide ou manquante : {}", field.getColumnName());
                    throw new MissingColumnException(field.getColumnName());
                }
            } catch (IllegalArgumentException e) {
                log.warn("Colonne obligatoire manquante dans le CSV : {}", field.getColumnName());
                throw new MissingColumnException(field.getColumnName());
            }
        }

        List<String> extraColumns = Arrays.stream(fieldSet.getNames())
                .filter(name -> Arrays.stream(ScpiField.values())
                        .noneMatch(f -> f.getColumnName().equals(name)))
                .toList();

        if (!extraColumns.isEmpty()) {
            extraColumns.forEach(col -> log.info("Colonne supplémentaire ignorée : {}", col));
        }

        return true;
    }
}

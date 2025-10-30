package fr.checkconsulting.scpiinvapi.batch.validator;

import fr.checkconsulting.scpiinvapi.batch.exception.MissingColumnException;
import fr.checkconsulting.scpiinvapi.batch.reporterrors.BatchErrorCollector;
import fr.checkconsulting.scpiinvapi.model.enums.ScpiField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CsvValidator {
    private final BatchErrorCollector errorCollector;

    public  boolean validate(FieldSet fieldSet) {
        String demembrementValue = fieldSet.readString(ScpiField.DEMEMBREMENT.getColumnName());
        String decoteDemembrementValue = fieldSet.readString(ScpiField.DECOTE_DEMEMBREMENT.getColumnName());

        for (ScpiField field : ScpiField.values()) {
            if (field == ScpiField.DECOTE_DEMEMBREMENT) {
                if ("Oui".equalsIgnoreCase(demembrementValue) &&
                        (decoteDemembrementValue == null || decoteDemembrementValue.isBlank())) {
                    log.info("Colonne decote_demembrement obligatoire quand demembrement = Oui");
                    throw new MissingColumnException(field.getColumnName());
                }
                continue;
            }

            try {
                String value = fieldSet.readString(field.getColumnName());
                if (value == null || value.isBlank()) {
                    log.info("Colonne obligatoire vide ou manquante : {}", field.getColumnName());
                        errorCollector.addError(fieldSet.getValues().length, "COLONNE_MANQUANTE",
                                "Colonne obligatoire vide ou manquante : " + field.getColumnName());
                    throw new MissingColumnException(field.getColumnName());
                }
            } catch (IllegalArgumentException e) {
                log.info("Colonne obligatoire manquante dans le CSV : {}", field.getColumnName());
                throw new MissingColumnException(field.getColumnName());
            }
        }

        List<String> extraColumns = Arrays.stream(fieldSet.getNames())
                .filter(name -> Arrays.stream(ScpiField.values())
                        .noneMatch(f -> f.getColumnName().equals(name)))
                .toList();

        if (!extraColumns.isEmpty()) {
            extraColumns.forEach(col -> {
                log.info("Colonne supplémentaire ignorée : {}", col);
                errorCollector.addError(0, "COLONNE_SUPPLEMENTAIRE",
                        "Colonne supplémentaire ignorée : " + col);
            });
        }

        return true;
    }


}

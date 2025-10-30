package fr.checkconsulting.scpiinvapi.batch.reader;

import fr.checkconsulting.scpiinvapi.batch.validator.CsvValidator;
import fr.checkconsulting.scpiinvapi.dto.request.ScpiDto;
import fr.checkconsulting.scpiinvapi.model.enums.ScpiField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScpiRequestFieldSetMapper implements FieldSetMapper<ScpiDto> {

 private final CsvValidator csvValidator;
    @NotNull
    @Override
    public ScpiDto mapFieldSet(FieldSet fieldSet) {
        String[] allColumnNames = fieldSet.getNames();

        List<String> extraColumns = Arrays.stream(allColumnNames)
                .filter(name -> Arrays.stream(ScpiField.values())
                        .noneMatch(f -> f.getColumnName().equals(name)))
                .toList();

        if (!extraColumns.isEmpty()) {
            extraColumns.forEach(col -> log.info("Colonne(s) supplémentaire(s) : {}", col));
        }
        csvValidator.validate(fieldSet);

        return ScpiDto.builder()
                .name(fieldSet.readString(ScpiField.NOM.getColumnName()))
                .distributedRate(fieldSet.readString(ScpiField.TAUX_DISTRIBUTION.getColumnName()))
                .sharePrice(fieldSet.readString(ScpiField.PRIX_PART.getColumnName()))
                .reconstitutionValue(fieldSet.readString(ScpiField.VALEUR_RECONSTITUTION.getColumnName()))
                .minimumSubscription(fieldSet.readInt(ScpiField.MINIMUM_SOUSCRIPTION.getColumnName()))
                .manager(fieldSet.readString(ScpiField.GERANT.getColumnName()))
                .capitalization(fieldSet.readBigDecimal(ScpiField.CAPITALISATION.getColumnName()))
                .subscriptionFees(fieldSet.readBigDecimal(ScpiField.FRAIS_SOUSCRIPTION.getColumnName()))
                .managementFees(fieldSet.readBigDecimal(ScpiField.FRAIS_GESTION.getColumnName()))
                .enjoymentDelay(fieldSet.readInt(ScpiField.DELAI_JOUISSANCE.getColumnName()))
                .iban(fieldSet.readString(ScpiField.IBAN.getColumnName()))
                .bic(fieldSet.readString(ScpiField.BIC.getColumnName()))
                .scheduledPayment(fieldSet.readString(ScpiField.VERSEMENT_PROGRAMME.getColumnName()))
                .rentFrequency(fieldSet.readString(ScpiField.FREQUENCE_LOYERS.getColumnName()))
                .cashback(fieldSet.readInt(ScpiField.CASHBACK.getColumnName()))
                .advertising(fieldSet.readString(ScpiField.PUBLICITE.getColumnName()))
                .dismemberment(fieldSet.readString(ScpiField.DEMEMBREMENT.getColumnName()))
                .locations(fieldSet.readString(ScpiField.LOCALISATION.getColumnName()))
                .sectors(fieldSet.readString(ScpiField.SECTEURS.getColumnName()))
                .dismembermentDiscounts(fieldSet.readString(ScpiField.DECOTE_DEMEMBREMENT.getColumnName()))
                .build();
    }
}

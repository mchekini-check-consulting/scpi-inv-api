package fr.checkconsulting.scpiinvapi.batch.reader;

import fr.checkconsulting.scpiinvapi.batch.exception.MissingColumnException;
import fr.checkconsulting.scpiinvapi.batch.reportErrors.BatchErrorCollector;
import fr.checkconsulting.scpiinvapi.dto.request.ScpiDto;
import fr.checkconsulting.scpiinvapi.model.enums.ScpiField;
import fr.checkconsulting.scpiinvapi.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScpiItemReader {

    private final ScpiRequestFieldSetMapper fieldSetMapper;
    private final BatchErrorCollector errorCollector;
    private final MinioService minioService;
    private final Environment environment;

    public ScpiItemReader(
            ScpiRequestFieldSetMapper fieldSetMapper,
            BatchErrorCollector errorCollector,
            MinioService minioService,
            Environment environment) {
        this.fieldSetMapper = fieldSetMapper;
        this.errorCollector = errorCollector;
        this.minioService = minioService;
        this.environment = environment;
    }

    @Bean
    public FlatFileItemReader<ScpiDto> reader() throws Exception {
        List<String> expectedColumns = List.of(
                ScpiField.NOM.getColumnName(),
                ScpiField.TAUX_DISTRIBUTION.getColumnName(),
                ScpiField.MINIMUM_SOUSCRIPTION.getColumnName(),
                ScpiField.LOCALISATION.getColumnName(),
                ScpiField.SECTEURS.getColumnName(),
                ScpiField.PRIX_PART.getColumnName(),
                ScpiField.CAPITALISATION.getColumnName(),
                ScpiField.GERANT.getColumnName(),
                ScpiField.FRAIS_SOUSCRIPTION.getColumnName(),
                ScpiField.FRAIS_GESTION.getColumnName(),
                ScpiField.DELAI_JOUISSANCE.getColumnName(),
                ScpiField.FREQUENCE_LOYERS.getColumnName(),
                ScpiField.VALEUR_RECONSTITUTION.getColumnName(),
                ScpiField.IBAN.getColumnName(),
                ScpiField.BIC.getColumnName(),
                ScpiField.DECOTE_DEMEMBREMENT.getColumnName(),
                ScpiField.DEMEMBREMENT.getColumnName(),
                ScpiField.CASHBACK.getColumnName(),
                ScpiField.VERSEMENT_PROGRAMME.getColumnName(),
                ScpiField.PUBLICITE.getColumnName()
        );


        String activeProfile = Arrays.stream(environment.getActiveProfiles())
                .findFirst()
                .orElse("int");
        String bucketName = activeProfile.equals("int") ? "int-data" : "qua-data";
        byte[] csvBytes = minioService.downloadFileAsBytes("scpi.csv", bucketName);
        validateCsvHeaders(new ByteArrayInputStream(csvBytes), expectedColumns);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(csvBytes));

        return new FlatFileItemReaderBuilder<ScpiDto>()
                .name("scpiRequestItemReader")
                .resource(resource)
                .encoding(StandardCharsets.UTF_8.name())
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names(expectedColumns.toArray(new String[0]))
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    private void validateCsvHeaders(InputStream inputStream, List<String> expectedColumns) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = br.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new MissingColumnException("Le fichier CSV est vide ou sans en-tête.");
            }

            List<String> actualHeaders = Arrays.stream(headerLine.split(","))
                    .map(String::trim)
                    .toList();

            List<String> missing = expectedColumns.stream()
                    .filter(col -> actualHeaders.stream().noneMatch(h -> h.equalsIgnoreCase(col)))
                    .collect(Collectors.toList());

            List<String> extra = actualHeaders.stream()
                    .filter(h -> expectedColumns.stream().noneMatch(col -> col.equalsIgnoreCase(h)))
                    .collect(Collectors.toList());

            if (!missing.isEmpty()) {
                missing.forEach(col -> errorCollector.addError(1, "COLONNE_MANQUANTE",
                        "Colonne manquante dans le CSV : " + col));

                log.error("Colonnes manquantes dans le CSV : {}", missing);
                throw new MissingColumnException("Colonnes manquantes : " + String.join(", ", missing));
            }

            if (!extra.isEmpty()) {
                extra.forEach(col -> errorCollector.addError(1, "COLONNE_SUPPLÉMENTAIRE",
                        "Colonne supplémentaire ignorée : " + col));
                log.warn("Colonnes supplémentaires détectées (ignorées) : {}", extra);
            }

            log.info("Vérification des colonnes CSV réussie : toutes les colonnes attendues sont présentes.");
        } catch (IOException e) {
            throw new MissingColumnException("Erreur lors de la lecture du fichier CSV : " + e.getMessage());
        }
    }
}

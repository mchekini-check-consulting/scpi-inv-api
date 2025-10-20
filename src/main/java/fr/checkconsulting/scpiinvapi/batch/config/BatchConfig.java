package fr.checkconsulting.scpiinvapi.batch.config;

import fr.checkconsulting.scpiinvapi.batch.constants.ScpiImportConstants;
import fr.checkconsulting.scpiinvapi.batch.exceptions.csvfille.CsvValidationException;
import fr.checkconsulting.scpiinvapi.batch.listener.CsvErrorReportListener;
import fr.checkconsulting.scpiinvapi.batch.mappers.ScpiFieldSetMapper;
import fr.checkconsulting.scpiinvapi.batch.processor.ScpiItemProcessor;
import fr.checkconsulting.scpiinvapi.batch.writter.ScpiItemWriter;
import fr.checkconsulting.scpiinvapi.dtos.requests.ScpiCSVDTORequest;
import fr.checkconsulting.scpiinvapi.models.entities.Scpi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableBatchProcessing
@Slf4j
@RequiredArgsConstructor
public class BatchConfig {
    @Bean
    public FlatFileItemReader<ScpiCSVDTORequest> scpiReader() throws IOException {


        List<String> headersForTokenizer = validateAndNormalizeHeaders();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setQuoteCharacter('"');
        tokenizer.setStrict(false);
        tokenizer.setNames(headersForTokenizer.toArray(String[]::new));

        FlatFileItemReader<ScpiCSVDTORequest> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource(ScpiImportConstants.CSV_SCPI_PATH));
        reader.setLinesToSkip(1);

        reader.setLineMapper((line, lineNumber) -> {
            var fieldSet = tokenizer.tokenize(line);
            var mapper = new ScpiFieldSetMapper();
            try {
                var scpi = mapper.mapFieldSet(fieldSet);
                scpi.setLineNumber((int) lineNumber);
                return scpi;
            } catch (Exception e) {
                log.error("Erreur lors du mappage du CSV à la ligne {} : {}", lineNumber, e.getMessage());
                throw new CsvValidationException(
                        "Erreur à la ligne " + lineNumber + " : " + e.getMessage(), e
                );
            }
        });
        return reader;

    }

    private List<String> validateAndNormalizeHeaders() throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new java.io.InputStreamReader(
                        new ClassPathResource(ScpiImportConstants.CSV_SCPI_PATH).getInputStream()
                ))) {

            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new RuntimeException("Le fichier CSV est vide ou sans colonnes");
            }
            List<String> actualHeadersRaw = Arrays.stream(headerLine.split(","))
                    .map(h -> h.trim().replace("\"", ""))
                    .toList();

            List<String> actualLower = actualHeadersRaw.stream()
                    .map(String::toLowerCase)
                    .toList();


            List<String> missing = ScpiImportConstants.EXPECTED_HEADERS_LOWER.stream()
                    .filter(expected -> actualLower.stream().noneMatch(a -> a.equals(expected)))
                    .toList();

            if (!missing.isEmpty()) {
                log.error("Colonnes manquantes : {}", missing);
                throw new RuntimeException("Structure CSV invalide, colonnes manquantes : " + missing);
            }

            List<String> extras = actualLower.stream()
                    .filter(h -> ScpiImportConstants.EXPECTED_HEADERS_LOWER.stream().noneMatch(e -> e.equals(h)))
                    .toList();
            if (!extras.isEmpty()) {
                log.warn("Colonnes supplémentaires détectées (ignorées) : {}", extras);
            }

            return actualHeadersRaw;
        }
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        input = input.trim();
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    @Bean
    public Step importScpiStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               FlatFileItemReader<ScpiCSVDTORequest> reader,
                               ScpiItemProcessor processor,
                               ScpiItemWriter writer,
                               CsvErrorReportListener errorListener) {
        return new StepBuilder("importScpiStep", jobRepository)
                .<ScpiCSVDTORequest, Scpi>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(errorListener)
                .build();
    }

    @Bean
    public Job importScpiJob(JobRepository jobRepository, Step importScpiStep) {
        return new JobBuilder("importScpiJob", jobRepository)
                .start(importScpiStep)
                .build();
    }
}

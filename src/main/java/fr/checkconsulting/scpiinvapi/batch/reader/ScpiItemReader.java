package fr.checkconsulting.scpiinvapi.batch.reader;

import fr.checkconsulting.scpiinvapi.dto.request.ScpiDto;
import fr.checkconsulting.scpiinvapi.model.enums.ScpiField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ScpiItemReader {

    private final ScpiRequestFieldSetMapper fieldSetMapper;
    private final ReadCsvFile readCsvFile;

    public ScpiItemReader(
            ScpiRequestFieldSetMapper fieldSetMapper,
            ReadCsvFile readCsvFile) {
        this.fieldSetMapper = fieldSetMapper;

        this.readCsvFile = readCsvFile;
    }

    @Bean
    public FlatFileItemReader<ScpiDto> reader() throws Exception {
        List<String> expectedColumns = getExpectedColumns();
        InputStreamResource resource = getCsvResource();

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

    private List<String> getExpectedColumns() {
        return Arrays.stream(ScpiField.values())
                .map(ScpiField::getColumnName)
                .toList();
    }

    private InputStreamResource getCsvResource() throws Exception {
        return readCsvFile.readCsv();

    }

}

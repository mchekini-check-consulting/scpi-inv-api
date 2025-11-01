package fr.checkconsulting.scpiinvapi.batch.reader;

import org.springframework.core.io.InputStreamResource;

public interface ReadCsvFile {
    InputStreamResource readCsv() throws Exception;

}

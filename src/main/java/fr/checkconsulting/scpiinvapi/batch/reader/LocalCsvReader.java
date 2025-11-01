package fr.checkconsulting.scpiinvapi.batch.reader;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;

@Component
@Profile({"local", "test"})
public class LocalCsvReader implements ReadCsvFile {
    @Override
    public InputStreamResource readCsv() throws FileNotFoundException {
        InputStream localStream = getClass().getClassLoader().getResourceAsStream("data/scpi.csv");
        if (localStream == null) {
            throw new FileNotFoundException("Fichier data/scpi.csv introuvable dans les ressources !");
        }
        return new InputStreamResource(localStream);
    }
}

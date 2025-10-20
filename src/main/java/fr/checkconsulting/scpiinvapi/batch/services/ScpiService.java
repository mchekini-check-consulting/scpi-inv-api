package fr.checkconsulting.scpiinvapi.batch.services;

import fr.checkconsulting.scpiinvapi.models.entities.Scpi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScpiService {
    public void saveScpi(Scpi scpi) {
        log.info("je suis dans le service pr sauvegarder les scpis");
    }
}

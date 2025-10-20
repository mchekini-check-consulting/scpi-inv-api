package fr.checkconsulting.scpiinvapi.batch.writter;

import fr.checkconsulting.scpiinvapi.batch.services.ScpiService;
import fr.checkconsulting.scpiinvapi.dtos.requests.ScpiCSVDTORequest;
import fr.checkconsulting.scpiinvapi.models.entities.Scpi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class ScpiItemWriter implements ItemWriter<Scpi> {
    private final ScpiService scpiService;

    @Override
    public void write(Chunk<? extends Scpi> chunk) throws Exception {
        List<? extends Scpi> items = chunk.getItems();
        log.info("Sauvegarde  de {} SCPI(s) validées dans la table", items.size());

        for (Scpi scpi : items) {
            try {
                scpiService.saveScpi(scpi);
            } catch (Exception e) {
                log.error("Erreur lors de la sauvegarde de la SCPI '{}' : {}",
                        scpi.getNom(),
                        e.getMessage(), e);
            }
        }
    }
}

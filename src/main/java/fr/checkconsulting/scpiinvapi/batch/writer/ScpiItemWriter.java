package fr.checkconsulting.scpiinvapi.batch.writer;

import fr.checkconsulting.scpiinvapi.batch.mappers.ScpiMapper;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.repository.ScpiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScpiItemWriter implements ItemWriter<Scpi> {

    private final ScpiRepository scpiRepository;
    private final ScpiMapper scpiMapper;

    @Override
    public void write(Chunk<? extends Scpi> chunk) {
        List<? extends Scpi> items = chunk.getItems();

        if (items.isEmpty()) return;

        log.info("===== Début du traitement de {} SCPI(s) =====", items.size());

        int createdCount = 0;
        int updatedCount = 0;

        for (Scpi scpi : items) {
            Optional<Scpi> existingOpt = scpiRepository.findByName(scpi.getName());

            if (existingOpt.isPresent()) {
                Scpi existing = existingOpt.get();
                scpiMapper.updateScpi(existing, scpi);
                scpiRepository.save(existing);
                updatedCount++;
                log.debug("SCPI mise à jour : {}", existing.getName());
            } else {
                scpiRepository.save(scpi);
                createdCount++;
                log.debug("Nouvelle SCPI enregistrée : {}", scpi.getName());
            }
        }

        log.info("===== Fin du traitement =====");
        log.info("{} SCPI(s) ajoutée(s), {} SCPI(s) mise(s) à jour.\n", createdCount, updatedCount);
    }
}

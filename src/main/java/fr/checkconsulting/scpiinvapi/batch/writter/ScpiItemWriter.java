package fr.checkconsulting.scpiinvapi.batch.writter;

import fr.checkconsulting.scpiinvapi.dtos.requests.ScpiCSVDTORequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ScpiItemWriter implements ItemWriter<ScpiCSVDTORequest> {
    @Override
    public void write(Chunk<? extends ScpiCSVDTORequest> chunk) throws Exception {
////        private final ScpiService scpiService;

//           log.info("Écriture de {} SCPI(s) validées dans la base de données", items.size());
//
//           for (ScpiCSVDTORequest scpiDto : items) {
//                try {
//                    scpiService.saveScpiFromCsv(scpiDto);
//                } catch (Exception e) {
//                  log.error("Erreur lors de l'enregistrement de la SCPI '{}' (ligne {}) : {}",
//                            scpiDto.getNom(),
//                          scpiDto.getLineNumber(),
//                           e.getMessage());
//               }
//            }
        log.info("****************Écriture de {} SCPI(s) validées dans la base de données");

    }
}

package fr.checkconsulting.scpiinvapi.batch.writer;

import fr.checkconsulting.scpiinvapi.mapper.ScpiMapper;
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


        for (Scpi scpi : items) {
            try {
                Optional<Scpi> existingOpt = scpiRepository.findByName(scpi.getName());

                if (existingOpt.isPresent()) {
                    Scpi existing = existingOpt.get();
                    scpiMapper.updateScpi(existing, scpi);
                    scpiRepository.save(existing);
                } else {
                    scpiRepository.save(scpi);
                }
            }
            catch (Exception e) {
                log.debug("Error while saving scpi {}", scpi.getName(), e);
            }
        }
    }
}

package fr.checkconsulting.scpiinvapi;

import fr.checkconsulting.scpiinvapi.dto.response.HistoryDto;
import fr.checkconsulting.scpiinvapi.mapper.HistoryMapper;
import fr.checkconsulting.scpiinvapi.model.entity.History;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class HistoryMapperTest {

    private HistoryMapper mapper = Mappers.getMapper(HistoryMapper.class);

    @Test
    void shouldMapEntityToDto() {
        History entity = new History();
        entity.setModificationDate(LocalDateTime.of(2023, 10, 1, 12, 0));
        entity.setStatus("VALIDATED");
        entity.setInvestmentId(42);

        HistoryDto dto = mapper.entityToDto(entity);

        assertEquals(entity.getModificationDate(), dto.getModificationDate());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getInvestmentId(), dto.getInvestmentId());
    }

    @Test
    void shouldMapDtoToEntityIgnoringId() {
        HistoryDto dto = HistoryDto.builder()
                .modificationDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .status("PENDING")
                .investmentId(99)
                .build();

        History entity = mapper.dtoToEntity(dto);

        assertNull(entity.getId());
        assertEquals(dto.getModificationDate(), entity.getModificationDate());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getInvestmentId(), entity.getInvestmentId());
    }
}

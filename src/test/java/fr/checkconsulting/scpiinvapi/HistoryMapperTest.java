package fr.checkconsulting.scpiinvapi;

import fr.checkconsulting.scpiinvapi.dto.response.HistoryDto;
import fr.checkconsulting.scpiinvapi.mapper.HistoryMapper;
import fr.checkconsulting.scpiinvapi.model.entity.History;
import fr.checkconsulting.scpiinvapi.model.entity.Investment;
import fr.checkconsulting.scpiinvapi.model.enums.InvestmentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class HistoryMapperTest {

    private final HistoryMapper mapper = Mappers.getMapper(HistoryMapper.class);

    @Test
    void shouldMapEntityToDto() {
        // Given
        Investment investment = new Investment();
        investment.setId(42L);

        History entity = new History();
        entity.setInvestment(investment);
        entity.setModificationDate(LocalDateTime.of(2023, 10, 1, 12, 0));
        entity.setStatus(InvestmentStatus.SUCCESS);

        HistoryDto dto = mapper.entityToDto(entity);

        assertEquals(entity.getModificationDate(), dto.getModificationDate());
        assertEquals("SUCCESS", dto.getStatus());
        assertEquals(42L, dto.getInvestmentId());
    }

    @Test
    void shouldMapDtoToEntityIgnoringId() {
        HistoryDto dto = HistoryDto.builder()
                .modificationDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .status("PENDING")
                .investmentId(99L)
                .build();

        History entity = mapper.dtoToEntity(dto);

        assertNull(entity.getId());
        assertEquals(dto.getModificationDate(), entity.getModificationDate());
        assertEquals(InvestmentStatus.PENDING, entity.getStatus());
        assertNull(entity.getInvestment());
    }
}

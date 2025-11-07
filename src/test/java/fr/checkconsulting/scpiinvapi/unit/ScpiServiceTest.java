package fr.checkconsulting.scpiinvapi.unit;

import fr.checkconsulting.scpiinvapi.dto.response.ScpiDetailDto;
import fr.checkconsulting.scpiinvapi.mapper.ScpiMapper;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.model.entity.ScpiPartValues;
import fr.checkconsulting.scpiinvapi.repository.ScpiRepository;
import fr.checkconsulting.scpiinvapi.service.ScpiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScpiServiceTest {

    @Mock
    private ScpiRepository scpiRepository;

    @Mock
    private ScpiMapper scpiMapper;

    @InjectMocks
    private ScpiService scpiService;

    private Scpi scpi;


    @BeforeEach
    void setUp() {
        scpiRepository.deleteAll();

        scpi = Scpi.builder()
                .name("Comète")
                .manager("Alderan")
                .build();

        DistributionRate rate2023 = new DistributionRate(null, 2023, BigDecimal.valueOf(5.0), scpi);
        DistributionRate rate2024 = new DistributionRate(null, 2024, BigDecimal.valueOf(4.5), scpi);

        ScpiPartValues value2023 = new ScpiPartValues(null, 2023, BigDecimal.valueOf(200.00), BigDecimal.valueOf(201.65), scpi);
        ScpiPartValues value2024 = new ScpiPartValues(null, 2024, BigDecimal.valueOf(200.00), BigDecimal.valueOf(205.37), scpi);

        scpi.setDistributionRates(List.of(rate2023, rate2024));
        scpi.setScpiValues(List.of(value2023, value2024));

        scpiRepository.saveAndFlush(scpi);
    }

    @Test
    void shouldReturnScpiDetail_whenScpiExistsAndManagerMatches() {

        ScpiDetailDto scpiDto = new ScpiDetailDto();
        scpiDto.setName("Comète");

        when(scpiRepository.findByName("Comète")).thenReturn(Optional.of(scpi));
        when(scpiMapper.toScpiDetailDto(scpi)).thenReturn(scpiDto);

        ScpiDetailDto detail = scpiService.getScpiDetails("Comète", "Alderan");

        assertNotNull(detail);
        assertEquals("Comète", detail.getName());
        verify(scpiRepository).findByName("Comète");

    }

    @Test
    void shouldThrowException_whenScpiNotFound() {
        when(scpiRepository.findByName("scpiName")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                scpiService.getScpiDetails("scpiName", "autherManager")
        );

        assertTrue(ex.getMessage().contains("Aucune SCPI trouvée"));
    }


    @Test
    void shouldThrowException_whenManagerDoesNotMatch() {

        when(scpiRepository.findByName("Comète")).thenReturn(Optional.of(scpi));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                scpiService.getScpiDetails("Comète", "Remake")
        );

        assertTrue(ex.getMessage().contains("ne correspond pas"));
    }

}

package fr.checkconsulting.scpiinvapi.unit;

import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateChartResponse;
import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateDTOResponse;
import fr.checkconsulting.scpiinvapi.mapper.DistributionRateMapper;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.repository.DistributionRateRepository;
import fr.checkconsulting.scpiinvapi.service.DistributionRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DistributionRateServiceImplTest {

    @Mock
    private DistributionRateRepository distributionRateRepository;

    @Mock
    private DistributionRateMapper distributionRateMapper;


    @InjectMocks
    DistributionRateService distributionRateService;


    @Test
    void shouldReturnAvg3YearsAndNotInsufficientHistory() {
        Long scpiId = 1L;

        List<DistributionRate> distributionRates = List.of(
                DistributionRate.builder().distributionYear(2020).rate(BigDecimal.valueOf(4.0)).build(),
                DistributionRate.builder().distributionYear(2021).rate(BigDecimal.valueOf(5.0)).build(),
                DistributionRate.builder().distributionYear(2022).rate(BigDecimal.valueOf(6.0)).build()
        );


        List<DistributionRateDTOResponse> dtoList = distributionRates.stream()
                .map(d -> DistributionRateDTOResponse.builder()
                        .distributionYear(d.getDistributionYear())
                        .rate(d.getRate())
                        .build())
                .toList();


        when(distributionRateRepository.findAllByScpi_IdOrderByDistributionYearAsc(scpiId))
                .thenReturn(distributionRates);

        when(distributionRateMapper.toDtoList(distributionRates))
                .thenReturn(dtoList);

        DistributionRateChartResponse response = distributionRateService.findAllDistributionRateByScpiId(scpiId);

        assertNotNull(response);
        assertEquals(3, response.getRates().size());
        assertFalse(response.isInsufficientHistory());
        assertEquals(5.0, response.getAvg3Years(), 0.001);

        verify(distributionRateRepository).findAllByScpi_IdOrderByDistributionYearAsc(scpiId);
    }

    @Test
    void shouldDetectInsufficientHistoryWhenLessThanTwoRates() {

        Long scpiId = 2L;

        List<DistributionRate> distributionRates = List.of(
                DistributionRate.builder()
                        .distributionYear(2022)
                        .rate(BigDecimal.valueOf(4.5))
                        .build()
        );


        List<DistributionRateDTOResponse> dtoList = List.of(
                DistributionRateDTOResponse.builder()
                        .distributionYear(2022)
                        .rate(BigDecimal.valueOf(4.5))
                        .build()
        );

        when(distributionRateRepository.findAllByScpi_IdOrderByDistributionYearAsc(scpiId))
                .thenReturn(distributionRates);
        when(distributionRateMapper.toDtoList(distributionRates))
                .thenReturn(dtoList);

        DistributionRateChartResponse response = distributionRateService.findAllDistributionRateByScpiId(scpiId);

        assertNotNull(response);
        assertTrue(response.isInsufficientHistory());
        assertEquals(0.0, response.getAvg3Years());
    }

}

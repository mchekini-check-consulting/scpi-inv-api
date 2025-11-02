package fr.checkconsulting.scpiinvapi.unit;

import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateDTOResponse;
import fr.checkconsulting.scpiinvapi.mapper.DistributionRateMapper;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.repository.DistributionRateRepository;
import fr.checkconsulting.scpiinvapi.service.impl.DistributionRateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DistributionRateServiceImplTest {

    @Mock
    private  DistributionRateRepository distributionRateRepository;

    @Mock
    private  DistributionRateMapper distributionRateMapper;


    @InjectMocks
    DistributionRateServiceImpl distributionRateServiceImpl;

    @Test
    void shouldReturnDistributionRatesByScpiId() {
        Long scpiId = 1L;

        List<DistributionRate> distribrates = List.of(
                new DistributionRate(2L, 2023, BigDecimal.valueOf(5.0), null),
                new DistributionRate(1L, 2024, BigDecimal.valueOf(4.5), null)
        );


        List<DistributionRateDTOResponse> distribratesdtos = List.of(
                new DistributionRateDTOResponse(2L, 2023, BigDecimal.valueOf(5.0), null),
                new DistributionRateDTOResponse(1L, 2024, BigDecimal.valueOf(4.5), null)
        );

        when(distributionRateRepository.findAllByScpi_IdOrderByDistributionYearAsc(scpiId))
                .thenReturn(distribrates);

        when(distributionRateMapper.toDtoList(distribrates))
                .thenReturn(distribratesdtos);

        List<DistributionRateDTOResponse> distributionratedtoresponse = distributionRateServiceImpl.findAllDistributionRateByScpiId(scpiId);

        assertEquals(2, distributionratedtoresponse.size());
        assertEquals(2023, distributionratedtoresponse.get(0).getDistributionYear());
        assertEquals(2024, distributionratedtoresponse.get(1).getDistributionYear());

        assertEquals(BigDecimal.valueOf(4.5), distributionratedtoresponse.get(1).getRate());

        verify(distributionRateRepository, times(1))
                .findAllByScpi_IdOrderByDistributionYearAsc(scpiId);

        verify(distributionRateMapper, times(1)).toDtoList(distribrates);
    }

}

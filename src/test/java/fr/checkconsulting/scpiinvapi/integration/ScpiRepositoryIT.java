package fr.checkconsulting.scpiinvapi.integration;

import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.model.entity.ScpiPartValues;
import fr.checkconsulting.scpiinvapi.repository.ScpiRepository;
import fr.checkconsulting.scpiinvapi.service.ScpiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ScpiRepositoryIT {

    @Autowired
    private ScpiRepository scpiRepository;

    @Autowired
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
    void shouldFindScpiByName() {
        // WHEN
        Optional<Scpi> scpiFound = scpiRepository.findByName("Comète");

        // THEN
        assertThat(scpiFound)
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getManager()).isEqualTo("Alderan");
                    assertThat(found.getDistributionRates()).hasSize(2);
                    assertThat(found.getScpiValues()).hasSize(2);
                });
    }

    @Test
    void shouldReturnDetailsFromService() {
        // WHEN
        var resultScpi = scpiService.getScpiDetails("Comète", "Alderan");

        // THEN
        assertThat(resultScpi).isNotNull();
        assertThat(resultScpi.getName()).isEqualTo("Comète");
        assertThat(resultScpi.getManager()).isEqualTo("Alderan");
        assertThat(resultScpi.getSharePrice()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
        assertThat(resultScpi.getDistributionRate()).isEqualByComparingTo(BigDecimal.valueOf(4.5));
    }

    @Test
    void shouldReturnEmptyWhenScpiNotFound() {
        Optional<Scpi> result = scpiRepository.findByName("NonExistante");
        assertThat(result).isEmpty();
    }
}

package fr.checkconsulting.scpiinvapi.integration;

import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.repository.DistributionRateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class DistributionRateRepositoryIT {

    @Autowired
    private DistributionRateRepository distributionRateRepository ;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void shouldFindDistributionRatesByScpiIdOrderedByYear() {
        Scpi scpi = Scpi.builder()
                .name("Test SCPI")
                .build();
        testEntityManager.persist(scpi);

        DistributionRate r1 = DistributionRate.builder()
                .distributionYear(2023)
                .rate(new BigDecimal("5.10"))
                .scpi(scpi)
                .build();
        DistributionRate r2 = DistributionRate.builder()
                .distributionYear(2022)
                .rate(new BigDecimal("4.90"))
                .scpi(scpi)
                .build();
        testEntityManager.persist(r1);
        testEntityManager.persist(r2);
        testEntityManager.flush();

        List<DistributionRate> distribrates =
                distributionRateRepository.findAllByScpi_IdOrderByDistributionYearAsc(scpi.getId());

        assertThat(distribrates).hasSize(2);
        assertThat(distribrates.get(0).getDistributionYear()).isEqualTo(2022);
        assertThat(distribrates.get(1).getDistributionYear()).isEqualTo(2023);
    }
}

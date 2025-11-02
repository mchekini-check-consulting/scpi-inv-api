package fr.checkconsulting.scpiinvapi.repository;

import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributionRateRepository extends JpaRepository<DistributionRate, Long> {

     List <DistributionRate> findAllByScpi_IdOrderByDistributionYearAsc(Long scpiId);
}

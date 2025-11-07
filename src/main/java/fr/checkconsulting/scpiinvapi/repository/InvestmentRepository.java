package fr.checkconsulting.scpiinvapi.repository;

import fr.checkconsulting.scpiinvapi.model.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByInvestorUserIdAndScpiId(String userId, Long scpiId);
}
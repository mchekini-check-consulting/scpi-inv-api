package fr.checkconsulting.scpiinvapi.repository;

import fr.checkconsulting.scpiinvapi.model.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    
    List<Investment> findByInvestorUserIdAndScpiId(String userId, Long scpiId);

    List<Investment> findByInvestorUserIdOrderByInvestmentDateDesc(String userId);

    List<Investment> findByInvestorUserIdOrderByInvestmentDateAsc(String userId);
    
    List<Investment> findByInvestorUserIdOrderByInvestmentAmountDesc(String userId);

    @Query("SELECT SUM(i.investmentAmount) FROM Investment i WHERE i.investor.userId = :userId")
    BigDecimal calculateTotalInvestedAmount(@Param("userId") String userId);
    

    @Query("SELECT COUNT(DISTINCT i.scpi.id) FROM Investment i WHERE i.investor.userId = :userId")
    Long countDistinctScpisByInvestorUserId(@Param("userId") String userId);
}
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
    
    List<Investment> findByUserEmailAndScpiId(String userId, Long scpiId);

    List<Investment> findByUserEmailOrderByInvestmentDateDesc(String userId);

    List<Investment> findByUserEmailOrderByInvestmentDateAsc(String userId);
    
    List<Investment> findByUserEmailOrderByInvestmentAmountDesc(String userId);

    @Query("SELECT SUM(i.investmentAmount) FROM Investment i WHERE i.userEmail = :userEmail")
    BigDecimal calculateTotalInvestedAmount(@Param("userEmail") String userEmail);


    boolean existsByUserEmailAndScpiId(String userEmail, Long scpiId);

}
package fr.checkconsulting.scpiinvapi.repository;

import fr.checkconsulting.scpiinvapi.dto.response.HistoryDto;
import fr.checkconsulting.scpiinvapi.model.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("""
    select new fr.checkconsulting.scpiinvapi.dto.response.HistoryDto(
        h.modificationDate,
        (select min(h2.modificationDate)
         from History h2
         where h2.investment.id = h.investment.id),
        h.status,
        h.investment.id
    )
    from History h
    where h.modificationDate = (
        select max(h3.modificationDate)
        from History h3
        where h3.investment.id = h.investment.id
    )
    order by h.investment.id
    """)
    List<HistoryDto> findLatestHistoryPerInvestment();


    List<History> findByInvestmentId(Long investmentId);
}

package fr.checkconsulting.scpiinvapi.repository;

import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScpiRepository extends JpaRepository<Scpi, Long> {

    Optional<Scpi> findByName(String name);

    List<Scpi> findByDismembermentIsFalse();
}

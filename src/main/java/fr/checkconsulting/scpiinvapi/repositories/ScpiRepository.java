package fr.checkconsulting.scpiinvapi.repositories;

import fr.checkconsulting.scpiinvapi.entities.Scpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScpiRepository extends JpaRepository<Scpi, Long> {
}

package fr.checkconsulting.scpiinvapi.repository;

import fr.checkconsulting.scpiinvapi.model.entity.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimulationRepository  extends JpaRepository<Simulation, Long> {

    List<Simulation> findAllByUserEmailOrderByCreatedAtDesc(String userEmail);

    Optional<Simulation> findByIdAndUserEmail(Long id, String userEmail);

}

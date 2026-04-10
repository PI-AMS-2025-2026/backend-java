package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.RecursoSala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecursoSalaRepository extends JpaRepository<RecursoSala, Long> {

    boolean existsBySalaIdAndRecursoId(Long salaId, Long recursoId);

    Page<RecursoSala> findBySalaId(Long salaId, Pageable pageable);

    Page<RecursoSala> findByRecursoId(Long recursoId, Pageable pageable);

}
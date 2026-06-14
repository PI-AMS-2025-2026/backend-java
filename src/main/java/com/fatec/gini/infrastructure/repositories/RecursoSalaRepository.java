package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.RecursoSala;


public interface RecursoSalaRepository extends JpaRepository<RecursoSala, Long> {

    boolean existsBySalaIdAndRecursoId(Long salaId, Long recursoId);

    @Query("""
        SELECT rs FROM RecursoSala rs
        WHERE (:salaId IS NULL OR rs.sala.id = :salaId)
        AND (:recursoId IS NULL OR rs.recurso.id = :recursoId)
    """)
    Page<RecursoSala> buscarPorFiltros(
            @Param("salaId") Long salaId,
            @Param("recursoId") Long recursoId,
            Pageable pageable
    );
}
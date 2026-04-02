package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.horario.domain.entities.GradeHoraria;

public interface GradeHorariaRepository extends JpaRepository<GradeHoraria, Long> {

    @Query("""
            SELECT g FROM GradeHoraria g
            WHERE (:cursoId IS NULL OR g.curso.id = :cursoId)
            AND (:periodoLetivoId IS NULL OR g.periodoLetivo.id = :periodoLetivoId)
            AND (:status IS NULL OR g.status = :status)
            """)
    Page<GradeHoraria> buscarComFiltros(
            @Param("cursoId") Long cursoId,
            @Param("periodoLetivoId") Long periodoLetivoId,
            @Param("status") String status,
            Pageable pageable
    );

}
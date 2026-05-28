package com.fatec.gini.infrastructure.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fatec.gini.domain.entities.GradeHoraria;
import com.fatec.gini.domain.entities.Status;

@Repository
public interface GradeHorariaRepository extends JpaRepository<GradeHoraria, Long> {

    @Query("""
            SELECT g FROM GradeHoraria g
            WHERE (:idCurso IS NULL OR g.curso.id = :idCurso)
            AND (:idPeriodoLetivo IS NULL OR g.periodoLetivo.id = :idPeriodoLetivo)
            AND (:status IS NULL OR g.status = :status)
            """)
    Page<GradeHoraria> buscarComFiltros(
            @Param("idCurso") Long idCurso,
            @Param("idPeriodoLetivo") Long idPeriodoLetivo,
            @Param("status") Status status,
            Pageable pageable
    );

    Optional<GradeHoraria> findTopByCursoIdAndPeriodoLetivoIdOrderByVersaoDesc(
            Long cursoId,
            Long periodoLetivoId
    );

    // Verifica se já existe grade ativa para o curso e período letivo
    boolean existsByCursoIdAndPeriodoLetivoIdAndStatus(
            Long cursoId,
            Long periodoLetivoId,
            Status status
    );

    // Verifica se já existe outra grade ativa para o curso e período letivo
    boolean existsByCursoIdAndPeriodoLetivoIdAndStatusAndIdNot(
            Long cursoId,
            Long periodoLetivoId,
            Status status,
            Long id
    );
}
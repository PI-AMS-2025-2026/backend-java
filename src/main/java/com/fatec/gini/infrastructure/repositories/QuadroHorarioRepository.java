package com.fatec.gini.infrastructure.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Status;


public interface QuadroHorarioRepository extends JpaRepository<QuadroHorario, Long> {

    @Query("""
            SELECT g FROM QuadroHorario g
            WHERE (:idCurso IS NULL OR g.curso.id = :idCurso)
            AND (:idPeriodoLetivo IS NULL OR g.periodoLetivo.id = :idPeriodoLetivo)
            AND (:status IS NULL OR g.status = :status)
            """)
    Page<QuadroHorario> buscarComFiltros(
            @Param("idCurso") Long idCurso,
            @Param("idPeriodoLetivo") Long idPeriodoLetivo,
            @Param("status") Status status,
            Pageable pageable
    );

    Optional<QuadroHorario> findTopByCursoIdAndPeriodoLetivoIdOrderByVersaoDesc(
            Long cursoId,
            Long periodoLetivoId
    );

    // Verifica se já existe quadro horário ativo para o curso e período letivo
    boolean existsByCursoIdAndPeriodoLetivoIdAndStatus(
            Long cursoId,
            Long periodoLetivoId,
            Status status
    );

    // Verifica se já existe outro quadro horário ativo para o curso e período letivo
    boolean existsByCursoIdAndPeriodoLetivoIdAndStatusAndIdNot(
            Long cursoId,
            Long periodoLetivoId,
            Status status,
            Long id
    );
}
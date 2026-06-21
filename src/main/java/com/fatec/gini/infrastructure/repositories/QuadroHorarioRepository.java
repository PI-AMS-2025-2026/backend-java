package com.fatec.gini.infrastructure.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;


public interface QuadroHorarioRepository extends JpaRepository<QuadroHorario, Long> {

    @Query("""
            SELECT g FROM QuadroHorario g
            WHERE (:idCurso IS NULL OR g.curso.id = :idCurso)
            AND (:idPeriodoAtividadeQuadro IS NULL OR g.periodoAtividadeQuadro.id = :idPeriodoAtividadeQuadro)
            AND (:status IS NULL OR g.status = :status)
            """)
    Page<QuadroHorario> buscarComFiltros(
            @Param("idCurso") Long idCurso,
            @Param("idPeriodoAtividadeQuadro") Long idPeriodoAtividadeQuadro,
            @Param("status") Status status,
            Pageable pageable
    );

    Optional<QuadroHorario> findTopByCursoIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(
            Long cursoId,
            Long periodoAtividadeQuadroId
    );

    // Verifica se já existe quadro horário ativo para o curso e período atividade quadro
    boolean existsByCursoIdAndPeriodoAtividadeQuadroIdAndStatus(
            Long cursoId,
            Long periodoAtividadeQuadroId,
            Status status
    );

    // Verifica se já existe outro quadro horário ativo para o curso e período atividade quadro
    boolean existsByCursoIdAndPeriodoAtividadeQuadroIdAndStatusAndIdNot(
            Long cursoId,
            Long periodoAtividadeQuadroId,
            Status status,
            Long id
    );
}
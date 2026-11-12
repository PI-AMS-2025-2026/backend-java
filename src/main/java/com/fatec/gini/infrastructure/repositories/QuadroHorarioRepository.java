package com.fatec.gini.infrastructure.repositories;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;

public interface QuadroHorarioRepository
                extends JpaRepository<QuadroHorario, Long> {

        @Query("""
                        SELECT g FROM QuadroHorario g
                        WHERE (:idCurso IS NULL OR g.curso.id = :idCurso)
                        AND (:idPeriodoAtividadeQuadro IS NULL
                             OR g.periodoAtividadeQuadro.id = :idPeriodoAtividadeQuadro)
                        AND (:status IS NULL OR g.status = :status)
                        AND (:versao IS NULL OR g.versao = :versao)
                        AND (:dataInicio IS NULL OR (g.dataCriacao >= :dataInicio AND g.dataCriacao < :dataFim))
                        AND (COALESCE(:busca, '') = '' OR LOWER(g.curso.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                             OR CAST(g.versao AS string) LIKE CONCAT('%', :busca, '%'))
                        """)
        Page<QuadroHorario> buscarComFiltros(
                        @Param("idCurso") Long idCurso,
                        @Param("idPeriodoAtividadeQuadro") Long idPeriodoAtividadeQuadro,
                        @Param("status") Status status,
                        @Param("versao") Integer versao,
                        @Param("dataInicio") LocalDateTime dataInicio,
                        @Param("dataFim") LocalDateTime dataFim,
                        @Param("busca") String busca,
                        Pageable pageable);

        Optional<QuadroHorario> findTopByCursoIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(
                        Long cursoId,
                        Long periodoAtividadeQuadroId);

        boolean existsByCursoIdAndPeriodoAtividadeQuadroIdAndStatus(
                        Long cursoId,
                        Long periodoAtividadeQuadroId,
                        Status status);

        boolean existsByCursoIdAndPeriodoAtividadeQuadroIdAndStatusAndIdNot(
                        Long cursoId,
                        Long periodoAtividadeQuadroId,
                        Status status,
                        Long id);

        List<QuadroHorario> findByCursoIdAndStatus(Long cursoId, Status status);

}

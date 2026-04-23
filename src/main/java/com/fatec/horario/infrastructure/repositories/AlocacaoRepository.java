package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.Alocacao;

import org.hibernate.query.Page;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface AlocacaoRepository extends JpaRepository<Alocacao, Long> {

    @Query("""
        SELECT COUNT(a) > 0
        FROM Alocacao a
        WHERE a.turma.id = :idTurma
        AND a.diaSemana.id = :idDiaSemana
        AND a.horario.id = :idHorario
    """)
    boolean existsConflitoTurmaHorario(
            @Param("idTurma") Long idTurma,
            @Param("idDiaSemana") Long idDiaSemana,
            @Param("idHorario") Long idHorario
    );

    boolean existsBySalaIdAndDiaSemanaIdAndHorarioId(Long salaId, Long diaId, Long horarioId);

    boolean existsByUsuarioIdAndDiaSemanaIdAndHorarioId(Long usuarioId, Long diaId, Long horarioId);

    @Query("""
        SELECT COUNT(d) > 0
        FROM DisponibilidadeProfessor d
        WHERE d.usuario.id = :usuarioId
        AND d.diaSemana.id = :diaId
        AND d.horario.id = :horarioId
    """)
    boolean verificarDisponibilidadeProfessor(
            @Param("usuarioId") Long usuarioId,
            @Param("diaId") Long diaId,
            @Param("horarioId") Long horarioId
    );

    @Query("""
        SELECT a FROM Alocacao a WHERE
        (:turmaId IS NULL OR a.turma.id = :turmaId) AND
        (:disciplinaId IS NULL OR a.disciplina.id = :disciplinaId) AND
        (:salaId IS NULL OR a.sala.id = :salaId) AND
        (:usuarioId IS NULL OR a.usuario.id = :usuarioId) AND
        (:diaSemanaId IS NULL OR a.diaSemana.id = :diaSemanaId) AND
        (:horarioId IS NULL OR a.horario.id = :horarioId) AND
        (:gradeId IS NULL OR a.gradeHoraria.id = :gradeId)
    """)
    Page<Alocacao> buscarPorFiltros(
            @Param("turmaId") Long turmaId,
            @Param("disciplinaId") Long disciplinaId,
            @Param("salaId") Long salaId,
            @Param("usuarioId") Long usuarioId,
            @Param("diaSemanaId") Long diaSemanaId,
            @Param("horarioId") Long horarioId,
            @Param("gradeId") Long gradeId,
            Pageable pageable
    );
}
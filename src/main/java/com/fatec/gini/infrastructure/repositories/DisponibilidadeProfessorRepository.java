package com.fatec.gini.infrastructure.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;

public interface DisponibilidadeProfessorRepository extends JpaRepository<DisponibilidadeProfessor, Long> {

    List<DisponibilidadeProfessor> findByBlocoHorarioId(Long blocoHorarioId);

    /**
     * Verifica duplicidade na criação (mesmo professor + dia da semana + bloco de horário).
     */
    boolean existsByProfessorIdAndDiaSemanaAndBlocoHorarioId(Long professorId, DiaSemana diaSemana, Long blocoHorarioId);

    /**
     * Verifica duplicidade na atualização, ignorando o próprio registro em edição (via IdNot).
     */
    boolean existsByProfessorIdAndDiaSemanaAndBlocoHorarioIdAndIdNot(Long professorId, DiaSemana diaSemana, Long blocoHorarioId, Long id);

    @Query("""
            SELECT d
            FROM DisponibilidadeProfessor d
            WHERE (:professorId IS NULL OR d.professor.id = :professorId)
            AND (:diaSemana IS NULL OR d.diaSemana = :diaSemana)
            AND (:blocoHorarioId IS NULL OR d.blocoHorario.id = :blocoHorarioId)
            AND (COALESCE(:professorNome, '') = '' OR LOWER(d.professor.nome) LIKE LOWER(CONCAT('%', :professorNome, '%')))
            AND (COALESCE(:horario, '') = '' OR
                 LOWER(CONCAT(CONCAT(CAST(d.blocoHorario.horaInicio AS string), ' - '), CAST(d.blocoHorario.horaFim AS string)))
                 LIKE LOWER(CONCAT('%', :horario, '%')))
            AND (COALESCE(:busca, '') = '' OR LOWER(d.professor.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                 OR LOWER(CAST(d.diaSemana AS string)) LIKE LOWER(CONCAT('%', :busca, '%'))
                 OR CAST(d.blocoHorario.horaInicio AS string) LIKE CONCAT('%', :busca, '%')
                 OR CAST(d.blocoHorario.horaFim AS string) LIKE CONCAT('%', :busca, '%'))
            AND (:cursoId IS NULL OR EXISTS (
                SELECT pd.id FROM ProfessorDisciplina pd
                WHERE pd.professor = d.professor AND pd.disciplina.curso.id = :cursoId
            ))
            """)
    Page<DisponibilidadeProfessor> buscarComFiltros(
            @Param("professorId") Long professorId,
            @Param("diaSemana") DiaSemana diaSemana,
            @Param("blocoHorarioId") Long blocoHorarioId,
            @Param("professorNome") String professorNome,
            @Param("horario") String horario,
            @Param("busca") String busca,
            @Param("cursoId") Long cursoId,
            Pageable pageable);

    /**
     * Verifica se existe registro que autorize a alocação do professor para o par DiaSemana/Horário.
     */
    @Query("""
            SELECT COUNT(d) > 0
            FROM DisponibilidadeProfessor d
            WHERE d.professor.id = :professorId
            AND d.diaSemana = :diaSemana
            AND d.blocoHorario.id = :blocoHorarioId
            """)
    boolean verificarDisponibilidadeProfessor(
            @Param("professorId") Long professorId,
            @Param("diaSemana") DiaSemana diaSemana,
            @Param("blocoHorarioId") Long blocoHorarioId);

    List<DisponibilidadeProfessor> findByProfessorIdIn(List<Long> professorIds);
}

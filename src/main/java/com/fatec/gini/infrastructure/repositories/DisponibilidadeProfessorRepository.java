package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.DisponibilidadeProfessor;


public interface DisponibilidadeProfessorRepository extends JpaRepository<DisponibilidadeProfessor, Long> {

       boolean existsByProfessorIdAndDiaSemanaIdAndBlocoHorarioId(Long professorId, Long diaId, Long blocoHorarioId);

       @Query("""
                            SELECT d
                            FROM DisponibilidadeProfessor d
                            WHERE (:professorId IS NULL OR d.professor.id = :professorId)
                            AND (:diaId IS NULL OR d.diaSemana.id = :diaId)
                            AND (:blocoHorarioId IS NULL OR d.blocoHorario.id = :blocoHorarioId)
                     """)
       Page<DisponibilidadeProfessor> buscarComFiltros(
                     @Param("professorId") Long professorId,
                     @Param("diaId") Long diaId,
                     @Param("blocoHorarioId") Long blocoHorarioId,
                     Pageable pageable);

       /**
        * REGRA DE DISPONIBILIDADE: "O professor pode trabalhar agora?"
        * A regra verifica se existe um registro prévio que autorize a alocação do
        * usuário para o par DiaSemana/Horário informado.
        * retorna true se o contador for > 0 (possui permissão), false caso contrário.
        */
       @Query("""
                            SELECT COUNT(d) > 0
                            FROM DisponibilidadeProfessor d
                            WHERE d.professor.id = :professorId
                            AND d.diaSemana.id = :diaId
                            AND d.blocoHorario.id = :blocoHorarioId
                     """)
       boolean verificarDisponibilidadeProfessor(
                     @Param("professorId") Long professorId,
                     @Param("diaId") Long diaId,
                     @Param("blocoHorarioId") Long blocoHorarioId);

}
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

       // CORREÇÃO: usado no service para impedir cadastro duplicado
       // (mesmo professor + dia da semana + bloco de horário)

       boolean existsByProfessorIdAndDiaSemanaAndBlocoHorarioId(Long professorId, DiaSemana diaSemana, Long blocoHorarioId);

       // CORREÇÃO: mesma checagem de duplicidade, usada na atualização (ignora o próprio registro via IdNot)
       boolean existsByProfessorIdAndDiaSemanaAndBlocoHorarioIdAndIdNot(Long professorId, DiaSemana diaSemana, Long blocoHorarioId, Long id);

       @Query("""
                     SELECT d
                     FROM DisponibilidadeProfessor d
                     WHERE (:professorId IS NULL OR d.professor.id = :professorId)
                     AND (:diaSemana IS NULL OR d.diaSemana = :diaSemana)
                     AND (:blocoHorarioId IS NULL OR d.blocoHorario.id = :blocoHorarioId)
                     """)
       Page<DisponibilidadeProfessor> buscarComFiltros(
                     @Param("professorId") Long professorId,
                     @Param("diaSemana") DiaSemana diaSemana,
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
                            AND d.diaSemana = :diaSemana
                            AND d.blocoHorario.id = :blocoHorarioId
                     """)
       boolean verificarDisponibilidadeProfessor(
                     @Param("professorId") Long professorId,
                     @Param("diaSemana") DiaSemana diaSemana,
                     @Param("blocoHorarioId") Long blocoHorarioId);


                     
       List<DisponibilidadeProfessor> findByProfessorIdIn(List<Long> professorIds);
}
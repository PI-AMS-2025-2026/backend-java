package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.DisponibilidadeProfessor;

@Repository
public interface DisponibilidadeProfessorRepository extends JpaRepository<DisponibilidadeProfessor, Long> {

       boolean existsByUsuarioIdAndDiaSemanaIdAndHorarioId(Long usuarioId, Long diaId, Long horarioId);

       @Query("""
                            SELECT d
                            FROM DisponibilidadeProfessor d
                            WHERE (:usuarioId IS NULL OR d.usuario.id = :usuarioId)
                            AND (:diaId IS NULL OR d.diaSemana.id = :diaId)
                            AND (:horarioId IS NULL OR d.horario.id = :horarioId)
                     """)
       Page<DisponibilidadeProfessor> buscarComFiltros(
                     @Param("usuarioId") Long usuarioId,
                     @Param("diaId") Long diaId,
                     @Param("horarioId") Long horarioId,
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
                            WHERE d.usuario.id = :usuarioId
                            AND d.diaSemana.id = :diaId
                            AND d.horario.id = :horarioId
                     """)
       boolean verificarDisponibilidadeProfessor(
                     @Param("usuarioId") Long usuarioId,
                     @Param("diaId") Long diaId,
                     @Param("horarioId") Long horarioId);

}
package com.fatec.gini.infrastructure.repositories;

import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.BlocoHorario;

public interface BlocoHorarioRepository extends JpaRepository<BlocoHorario, Long> {

        /**
         * Retorna horários aplicando filtros opcionais.
         *
         * Regras dos filtros:
         * - horaInicio: comparação exata.
         * - horaFim: comparação exata.
         * - duracao: comparação exata.
         *
         * Quando um parâmetro é null, o filtro correspondente é ignorado.
         */
        @Query("""
                        SELECT h
                        FROM BlocoHorario h
                        WHERE (:horaInicio IS NULL OR h.horaInicio = :horaInicio)
                          AND (:horaFim IS NULL OR h.horaFim = :horaFim)
                          AND (:duracao IS NULL OR h.duracao = :duracao)
                        ORDER BY h.horaInicio
                        """)
        Page<BlocoHorario> buscarPorFiltros(
                        @Param("horaInicio") LocalTime horaInicio,
                        @Param("horaFim") LocalTime horaFim,
                        @Param("duracao") Integer duracao,
                        Pageable pageable);

        /**
         * Usado para saber se já existe horario com mesmo inicio e fim
         * @param horaInicio
         * @param horaFim
         * @return true/false
         */
        boolean existsByHoraInicioAndHoraFim(
                        LocalTime horaInicio,
                        LocalTime horaFim);
}
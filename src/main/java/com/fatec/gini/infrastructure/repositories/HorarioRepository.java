package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fatec.gini.domain.entities.Horario;

import java.time.LocalTime;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

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
            FROM Horario h
            WHERE (:horaInicio IS NULL OR h.horaInicio = :horaInicio)
              AND (:horaFim IS NULL OR h.horaFim = :horaFim)
              AND (:duracao IS NULL OR h.duracao = :duracao)
            """)
    Page<Horario> buscarPorFiltros(
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim,
            @Param("duracao") Integer duracao,
            Pageable pageable);
}
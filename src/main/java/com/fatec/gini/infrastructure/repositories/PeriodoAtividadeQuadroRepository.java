package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.entities.Status;


public interface PeriodoAtividadeQuadroRepository extends JpaRepository<PeriodoAtividadeQuadro, Long> {

        /**
         * Retorna períodos atividade quadro aplicando filtros opcionais.
         *
         * Regras dos filtros:
         * - ano: comparação exata.
         * - periodo: comparação exata.
         * - status: comparação exata, respeitando maiúsculas/minúsculas.
         * - dataInicio: comparação exata.
         * - dataFim: comparação exata.
         *
         * Quando um parâmetro é null, o filtro correspondente é ignorado.
         */
        @Query("""
                        SELECT p
                        FROM PeriodoAtividadeQuadro p
                        WHERE (:ano IS NULL OR p.ano = :ano)
                          AND (:periodo IS NULL OR p.periodo = :periodo)
                                                                    AND (:status IS NULL OR p.status = :status)
                          AND (:dataInicio IS NULL OR p.dataInicio = :dataInicio)
                          AND (:dataFim IS NULL OR p.dataFim = :dataFim)
                        """)
        Page<PeriodoAtividadeQuadro> buscarPorFiltros(
                        @Param("ano") Integer ano,
                        @Param("periodo") Integer periodo,
                        @Param("status") Status status,
                        @Param("dataInicio") java.time.LocalDate dataInicio,
                        @Param("dataFim") java.time.LocalDate dataFim,
                        Pageable pageable);


        boolean existsByIdAndStatus(
                        Long id,
                        Status status);

}

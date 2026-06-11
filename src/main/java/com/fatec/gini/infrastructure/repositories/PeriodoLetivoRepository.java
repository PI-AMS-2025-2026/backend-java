package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.PeriodoLetivo;
import com.fatec.gini.domain.entities.Status;


public interface PeriodoLetivoRepository extends JpaRepository<PeriodoLetivo, Long> {

        /**
         * Retorna períodos letivos aplicando filtros opcionais.
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
                        FROM PeriodoLetivo p
                        WHERE (:ano IS NULL OR p.ano = :ano)
                          AND (:periodo IS NULL OR p.periodo = :periodo)
                                                                    AND (:status IS NULL OR p.status = :status)
                          AND (:dataInicio IS NULL OR p.dataInicio = :dataInicio)
                          AND (:dataFim IS NULL OR p.dataFim = :dataFim)
                        """)
        Page<PeriodoLetivo> buscarPorFiltros(
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

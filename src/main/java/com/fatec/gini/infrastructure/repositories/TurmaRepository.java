package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.Turma;


public interface TurmaRepository extends JpaRepository<Turma, Long> {

    @Query("""
           SELECT t FROM Turma t
           WHERE (:idCurso IS NULL OR t.curso.id = :idCurso)
             AND (:ano IS NULL OR t.ano = :ano)
             AND (:periodo IS NULL OR t.periodo = :periodo)
             AND (:codigo IS NULL OR LOWER(t.codigo) = LOWER(:codigo))
           """)
    Page<Turma> buscarPorFiltros(
            @Param("idCurso") Long idCurso,
            @Param("ano") Integer ano,
            @Param("periodo") Integer periodo,
            @Param("codigo") String codigo,
            Pageable pageable);
}
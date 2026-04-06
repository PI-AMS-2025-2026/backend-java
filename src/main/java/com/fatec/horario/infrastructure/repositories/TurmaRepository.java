package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.Turma;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    @Query("""
        SELECT t FROM Turma t
        WHERE (:idCurso IS NULL OR t.curso.idCurso = :idCurso)
        AND (:ano IS NULL OR t.ano = :ano)
        AND (:periodo IS NULL OR t.periodo = :periodo)
    """)
    Page<Turma> findByFilters(Long idCurso, Integer ano, Integer periodo, Pageable pageable);
}


package com.fatec.gini.infrastructure.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.Turma;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    boolean existsByCursoIdAndAnoAndPeriodo(
            Long cursoId,
            Integer ano,
            Integer periodo);

    boolean existsByCursoIdAndAnoAndPeriodoAndIdNot(
            Long cursoId,
            Integer ano,
            Integer periodo,
            Long id);

    @Query("""
           SELECT t FROM Turma t
           WHERE (:idCurso IS NULL OR t.curso.id = :idCurso)
             AND (:ano IS NULL OR t.ano = :ano)
             AND (:periodo IS NULL OR t.periodo = :periodo)
             AND (COALESCE(:codigo, '') = '' OR LOWER(t.codigo) = LOWER(:codigo))
             AND (:status IS NULL OR t.status = :status)
             AND (COALESCE(:busca, '') = '' OR LOWER(t.codigo) LIKE LOWER(CONCAT('%', :busca, '%'))
                  OR LOWER(t.curso.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                  OR CAST(t.ano AS string) LIKE CONCAT('%', :busca, '%')
                  OR CAST(t.periodo AS string) LIKE CONCAT('%', :busca, '%'))
           """)
    Page<Turma> buscarPorFiltros(
            @Param("idCurso") Long idCurso,
            @Param("ano") Integer ano,
            @Param("periodo") Integer periodo,
            @Param("codigo") String codigo,
            @Param("busca") String busca,
            @Param("status") com.fatec.gini.domain.models.Status status,
            Pageable pageable);

    List<Turma> findByCursoIdOrderByAnoAscPeriodoAscCodigoAsc(Long cursoId);

    Optional<Turma> findByIdAndCursoId(Long id, Long cursoId);
}

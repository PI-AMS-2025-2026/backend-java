package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.ProfessorDisciplina;


public interface ProfessorDisciplinaRepository extends JpaRepository<ProfessorDisciplina, Long> {


    @Query("""
        SELECT pd
        FROM ProfessorDisciplina pd
        WHERE (:professorId IS NULL OR pd.professor.id = :professorId)
          AND (:disciplinaId IS NULL OR pd.disciplina.id = :disciplinaId)
          AND (:cursoId IS NULL OR pd.disciplina.curso.id = :cursoId)
    """)
    Page<ProfessorDisciplina> buscarPorFiltros(
        @Param("professorId") Long professorId,
        @Param("disciplinaId") Long disciplinaId,
        @Param("cursoId") Long cursoId,
        Pageable pageable
    );

    boolean existsByProfessorIdAndDisciplinaCursoId(Long professorId, Long cursoId);

    boolean existsByProfessorIdAndDisciplinaId(
        @Param("idProfessor") Long idProfessor,
        @Param("idDisciplina") Long idDisciplina
    );

    boolean existsByDisciplinaId(Long disciplinaId);

    boolean existsByProfessorId(Long professorId);

}

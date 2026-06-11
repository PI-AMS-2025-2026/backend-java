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
        WHERE (:usuarioId IS NULL OR pd.usuario.id = :usuarioId)
          AND (:disciplinaId IS NULL OR pd.disciplina.id = :disciplinaId)
    """)
    Page<ProfessorDisciplina> buscarPorFiltros(
        @Param("usuarioId") Long usuarioId,
        @Param("disciplinaId") Long disciplinaId,
        Pageable pageable
    );

    boolean existsByUsuarioIdAndDisciplinaId(
        @Param("idProfessor") Long idProfessor,
        @Param("idDisciplina") Long idDisciplina
    );

    boolean existsByDisciplinaId(Long disciplinaId);

    boolean existsByUsuarioId(Long usuarioId);

}

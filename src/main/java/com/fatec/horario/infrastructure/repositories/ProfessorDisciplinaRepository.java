package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.ProfessorDisciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfessorDisciplinaRepository extends JpaRepository<ProfessorDisciplina, Long> {

    boolean existsByUsuarioIdUsuarioAndDisciplinaIdDisciplina(Long usuarioId, Long disciplinaId);

    Page<ProfessorDisciplina> findByUsuarioIdUsuario(Long usuarioId, Pageable pageable);

    Page<ProfessorDisciplina> findByDisciplinaIdDisciplina(Long disciplinaId, Pageable pageable);

        @Query("""
            SELECT pd
            FROM ProfessorDisciplina pd
            WHERE (:usuarioId IS NULL OR pd.usuario.idUsuario = :usuarioId)
              AND (:disciplinaId IS NULL OR pd.disciplina.idDisciplina = :disciplinaId)
            """)
        Page<ProfessorDisciplina> buscarPorFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("disciplinaId") Long disciplinaId,
            Pageable pageable);
}

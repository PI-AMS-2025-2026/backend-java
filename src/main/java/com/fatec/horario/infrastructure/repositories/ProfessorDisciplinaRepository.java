package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.ProfessorDisciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorDisciplinaRepository extends JpaRepository<ProfessorDisciplina, Long> {

    //TODO: verificar nescessidade de query customizadas e de paginação com filtro
    boolean existsByUsuarioIdAndDisciplinaId(Long usuarioId, Long disciplinaId);

    Page<ProfessorDisciplina> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<ProfessorDisciplina> findByDisciplinaId(Long disciplinaId, Pageable pageable);

        @Query("""
            SELECT pd
            FROM ProfessorDisciplina pd
                        WHERE (:usuarioId IS NULL OR pd.usuario.id = :usuarioId)
                            AND (:disciplinaId IS NULL OR pd.disciplina.id = :disciplinaId)
            """)
        Page<ProfessorDisciplina> buscarPorFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("disciplinaId") Long disciplinaId,
            Pageable pageable);
}

package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// classe "ProfessorDisciplina" não existe até o momento em que estou fazendo a issue
public interface ProfessorDisciplinaRepository extends JpaRepository<ProfessorDisciplina, Long> {

    @Query("""
        SELECT COUNT(pd) > 0 
        FROM ProfessorDisciplina pd
        WHERE pd.professor.id_usuario = :idProfessor
        AND pd.disciplina.id_disciplina = :idDisciplina
    """)
    boolean existsByProfessorAndDisciplina(
            @Param("idProfessor") Long idProfessor,
            @Param("idDisciplina") Long idDisciplina
    );
}
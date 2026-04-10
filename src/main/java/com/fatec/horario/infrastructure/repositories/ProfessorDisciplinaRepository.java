package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.ProfessorDisciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorDisciplinaRepository extends JpaRepository<ProfessorDisciplina, Long> {

    boolean existsByUsuarioIdUsuarioAndDisciplinaIdDisciplina(Long usuarioId, Long disciplinaId);

    Page<ProfessorDisciplina> findByUsuarioIdUsuario(Long usuarioId, Pageable pageable);

    Page<ProfessorDisciplina> findByDisciplinaIdDisciplina(Long disciplinaId, Pageable pageable);
}

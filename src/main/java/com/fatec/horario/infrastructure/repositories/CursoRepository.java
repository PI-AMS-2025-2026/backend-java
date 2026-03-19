package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
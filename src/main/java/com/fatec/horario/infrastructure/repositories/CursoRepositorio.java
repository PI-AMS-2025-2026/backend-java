package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.Curso;

@Repository
public interface CursoRepositorio extends JpaRepository<Curso, Long> {

}
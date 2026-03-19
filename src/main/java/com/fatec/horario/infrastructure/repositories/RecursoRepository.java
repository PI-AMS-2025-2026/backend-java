package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.horario.domain.entities.Recurso;

public interface RecursoRepository extends JpaRepository<Recurso, Long> {
    
}

package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaSemanaRepository extends JpaRepository<DiaSemana, Long> {
}
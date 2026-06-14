package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.gini.domain.entities.DiaSemana;


public interface DiaSemanaRepository extends JpaRepository<DiaSemana, Long> {
}
package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.horario.domain.entities.TipoSala;

public interface TipoSalaRepository extends JpaRepository<TipoSala, Long> {

}
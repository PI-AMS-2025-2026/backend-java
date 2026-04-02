package com.fatec.horario.infrastructure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.TipoSala;

@Repository
public interface TipoSalaRepository extends JpaRepository<TipoSala, Long> {

    // Busca tipos de sala pelo nome (ignora maiúsculo/minúsculo)
    List<TipoSala> findByNomeContainingIgnoreCase(String nome);
}
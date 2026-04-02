package com.fatec.horario.infrastructure.repositories;

import com.fatec.horario.domain.entities.Sala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {

    Page<Sala> findByTipoSalaId(Long tipoSalaId, Pageable pageable);

    Page<Sala> findByCapacidade(Integer capacidade, Pageable pageable);

    Page<Sala> findByTipoSalaIdAndCapacidade(Long tipoSalaId, Integer capacidade, Pageable pageable);
    
    Optional<Sala> findByCodigo(String codigo);
}
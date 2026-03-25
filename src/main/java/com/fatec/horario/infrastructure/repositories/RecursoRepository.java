package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.Recurso;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    // Filtro por nome com paginação
    Page<Recurso> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Filtro por tipo com paginação
    Page<Recurso> findByTipoContainingIgnoreCase(String tipo, Pageable pageable);

    // Filtro combinando nome + tipo
    Page<Recurso> findByNomeContainingIgnoreCaseAndTipoContainingIgnoreCase(
            String nome, String tipo, Pageable pageable);
}
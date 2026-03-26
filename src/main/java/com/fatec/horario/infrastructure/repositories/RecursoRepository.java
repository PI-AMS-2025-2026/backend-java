package com.fatec.horario.infrastructure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.Recurso;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

        // Filtro por nome com paginação
    List<Recurso> findByNomeContainingIgnoreCase(String nome);

    // Filtro por tipo com paginação
    List<Recurso> findByTipoContainingIgnoreCase(String tipo);

    // Filtro combinando nome + tipo
    List<Recurso> findByNomeContainingIgnoreCaseAndTipoContainingIgnoreCase(
            String nome, String tipo);
}
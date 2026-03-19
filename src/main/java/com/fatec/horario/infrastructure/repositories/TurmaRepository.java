package com.fatec.horario.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fatec.horario.domain.entities.Turma;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {

    /*
     * JpaRepository já fornece operações CRUD automaticamente:
     *
     * save()       -> criar/atualizar
     * findAll()    -> listar
     * findById()   -> buscar por id
     * deleteById() -> remover
     * existsById() -> verificar existência
     */

}

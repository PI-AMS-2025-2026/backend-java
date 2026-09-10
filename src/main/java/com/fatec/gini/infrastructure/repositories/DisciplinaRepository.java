package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.Disciplina;


public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    @Query("SELECT d FROM Disciplina d WHERE " +
           "(:nome IS NULL OR LOWER(d.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:idCurso IS NULL OR d.curso.id = :idCurso) AND " +
           "(:idTipoSala IS NULL OR d.tipoSala.id = :idTipoSala)")
    Page<Disciplina> findByFiltros(
        @Param("nome") String nome,
        @Param("idCurso") Long idCurso,
        @Param("idTipoSala") Long idTipoSala,
        Pageable pageable
    );
// CORREÇÃO: usados no service para bloquear codDisciplina duplicado com mensagem explícita
    boolean existsByCodDisciplina(String codDisciplina);
    boolean existsByCodDisciplinaAndIdNot(String codDisciplina, Long id);
}
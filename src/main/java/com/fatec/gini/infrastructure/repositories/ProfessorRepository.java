package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.Status;



public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    /**
     * Retorna usuários aplicando filtros opcionais.
     *
     * Regras dos filtros:
     * - nome: busca parcial, ignorando maiúsculas/minúsculas.
     * - email: busca parcial, ignorando maiúsculas/minúsculas.
     * - cidade: busca parcial, ignorando maiúsculas/minúsculas.
     * - status: comparação exata, respeitando maiúsculas/minúsculas.
     *
     * Quando um parâmetro é null, o filtro correspondente é ignorado.
     */
    @Query("""
        SELECT u
        FROM Professor u
        WHERE (:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
          AND (:cidade IS NULL OR LOWER(u.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))
          AND (:status IS NULL OR u.status = :status)
        """)
    Page<Professor> buscarPorFiltros(
        @Param("nome") String nome,
        @Param("email") String email,
        @Param("cidade") String cidade,
        @Param("status") Status status,
        Pageable pageable);



}
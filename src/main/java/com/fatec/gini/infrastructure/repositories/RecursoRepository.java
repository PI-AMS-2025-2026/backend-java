package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fatec.gini.domain.entities.Recurso;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    /**
     * Retorna recursos aplicando filtros opcionais.
     *
     * Regras dos filtros:
     * - nome: busca parcial, ignorando maiúsculas/minúsculas.
     * - tipo: busca parcial, ignorando maiúsculas/minúsculas.
     *
     * Quando um parâmetro é null, o filtro correspondente é ignorado.
     */
    @Query("""
            SELECT r
            FROM Recurso r
            WHERE (:nome IS NULL OR LOWER(r.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND (:tipo IS NULL OR LOWER(r.tipo) LIKE LOWER(CONCAT('%', :tipo, '%')))
            """)
    Page<Recurso> buscarPorFiltros(
            @Param("nome") String nome,
            @Param("tipo") String tipo,
            Pageable pageable);
}
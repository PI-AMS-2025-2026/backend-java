package com.fatec.gini.infrastructure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.TipoSala;


public interface TipoSalaRepository extends JpaRepository<TipoSala, Long> {

    /**
     * Retorna tipos de sala aplicando filtro opcional por nome.
     *
     * Regra do filtro:
     * - nome: busca parcial, ignorando maiúsculas/minúsculas.
     *
     * Quando o parâmetro é null, o filtro é ignorado.
     */
    @Query("""
            SELECT t
            FROM TipoSala t
            WHERE (:nome IS NULL OR LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
            """)
    List<TipoSala> buscarPorFiltro(@Param("nome") String nome);
}
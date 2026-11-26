package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.Recurso;

public interface RecursoRepository extends JpaRepository<Recurso, Long> {


  /* 
  Verifica se já existe um recurso com o mesmo nome.
   */
  boolean existsByNomeIgnoreCase(String nome);

  boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
    /**
     * Retorna recursos aplicando filtros opcionais.
     *
     * Regras dos filtros: - nome: busca parcial, ignorando
     * maiúsculas/minúsculas. - tipo: busca parcial, ignorando
     * maiúsculas/minúsculas.
     *
     * Quando um parâmetro é null, o filtro correspondente é ignorado.
     */
    @Query("""
        SELECT r
        FROM Recurso r
        WHERE (COALESCE(:nome, '') = '' OR LOWER(r.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:idTipoRecurso IS NULL OR r.tipoRecurso.id = :idTipoRecurso)
          AND (COALESCE(:busca, '') = '' OR LOWER(r.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
               OR LOWER(r.tipoRecurso.nome) LIKE LOWER(CONCAT('%', :busca, '%')))
        """)
    Page<Recurso> buscarPorFiltros(
            @Param("nome") String nome,
            @Param("idTipoRecurso") Long tipo,
            @Param("busca") String busca,
            Pageable pageable);
}

package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.HistoricoAlteracao;


public interface HistoricoAlteracaoRepository extends JpaRepository<HistoricoAlteracao, Long>{

    @Query("""
	    SELECT h
	    FROM HistoricoAlteracao h
	    WHERE (:alocacaoId IS NULL OR h.alocacao.id = :alocacaoId)
	      AND (:usuarioId IS NULL OR h.usuario.id = :usuarioId)
	    """)
    Page<HistoricoAlteracao> buscarPorFiltros(
	    @Param("alocacaoId") Long alocacaoId,
	    @Param("usuarioId") Long usuarioId,
	    Pageable pageable);
}

package com.fatec.gini.infrastructure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.HistoricoVersaoAlocacao;


public interface HistoricoVersaoAlocacaoRepository extends JpaRepository<HistoricoVersaoAlocacao, Long>{

    @Query("""
	    SELECT h
	    FROM HistoricoVersaoAlocacao h
	    WHERE (:alocacaoId IS NULL OR h.alocacao.id = :alocacaoId)
			  AND (:usuarioId IS NULL OR h.usuario.id = :usuarioId)
			  AND (COALESCE(:busca, '') = '' OR LOWER(h.usuario.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
			       OR LOWER(CAST(h.dataAlteracao AS string)) LIKE LOWER(CONCAT('%', :busca, '%'))
			       OR LOWER(h.campoAlterado) LIKE LOWER(CONCAT('%', :busca, '%'))
			       OR LOWER(COALESCE(h.valorAntigo, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
			       OR LOWER(h.valorNovo) LIKE LOWER(CONCAT('%', :busca, '%'))
			       OR LOWER(h.alocacao.disciplina.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
			       OR LOWER(h.alocacao.turma.codigo) LIKE LOWER(CONCAT('%', :busca, '%'))
			       OR LOWER(h.alocacao.sala.codigo) LIKE LOWER(CONCAT('%', :busca, '%'))
			       OR CAST(h.alocacao.id AS string) LIKE CONCAT('%', :busca, '%'))
			  AND (:cursoId IS NULL OR h.alocacao.quadroHorario.curso.id = :cursoId)
	    """)
    Page<HistoricoVersaoAlocacao> buscarPorFiltros(
	    @Param("alocacaoId") Long alocacaoId,
	    @Param("usuarioId") Long usuarioId,
	    @Param("busca") String busca,
	    @Param("cursoId") Long cursoId,
	    Pageable pageable);
}

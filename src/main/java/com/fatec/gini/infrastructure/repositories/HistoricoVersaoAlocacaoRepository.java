package com.fatec.gini.infrastructure.repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.gini.domain.entities.HistoricoVersaoAlocacao;

public interface HistoricoVersaoAlocacaoRepository extends JpaRepository<HistoricoVersaoAlocacao, Long> {

    @Query("""
            SELECT h FROM HistoricoVersaoAlocacao h
            WHERE (:alocacaoId IS NULL OR h.alocacaoIdOrigem = :alocacaoId)
              AND (:usuarioId IS NULL OR h.usuario.id = :usuarioId)
              AND (:cursoId IS NULL OR h.cursoId = :cursoId)
            """)
    Page<HistoricoVersaoAlocacao> buscarPorFiltros(
            @Param("alocacaoId") Long alocacaoId,
            @Param("usuarioId") Long usuarioId,
            @Param("cursoId") Long cursoId,
            Pageable pageable);

    @Modifying
    @Query("UPDATE HistoricoVersaoAlocacao h SET h.alocacao = null WHERE h.alocacao.id IN :alocacaoIds")
    int desvincularAlocacoes(@Param("alocacaoIds") Collection<Long> alocacaoIds);
}

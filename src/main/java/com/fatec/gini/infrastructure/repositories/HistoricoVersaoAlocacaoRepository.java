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
            SELECT h
            FROM HistoricoVersaoAlocacao h
            LEFT JOIN h.alocacao a
            LEFT JOIN a.disciplina d
            LEFT JOIN a.turma t
            LEFT JOIN a.sala s
            WHERE (:alocacaoId IS NULL OR h.alocacaoIdOrigem = :alocacaoId)
              AND (:usuarioId IS NULL OR h.usuario.id = :usuarioId)
              AND (:cursoId IS NULL OR h.cursoId = :cursoId)
              AND (COALESCE(:busca, '') = ''
                   OR LOWER(h.usuario.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(CAST(h.dataAlteracao AS string)) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(h.campoAlterado) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(COALESCE(h.valorAntigo, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(h.valorNovo) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(COALESCE(d.nome, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(COALESCE(t.codigo, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(COALESCE(s.codigo, '')) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR CAST(h.alocacaoIdOrigem AS string) LIKE CONCAT('%', :busca, '%'))
            """)
    Page<HistoricoVersaoAlocacao> buscarPorFiltros(
            @Param("alocacaoId") Long alocacaoId,
            @Param("usuarioId") Long usuarioId,
            @Param("busca") String busca,
            @Param("cursoId") Long cursoId,
            Pageable pageable);

    @Modifying
    @Query("UPDATE HistoricoVersaoAlocacao h SET h.alocacao = null WHERE h.alocacao.id IN :alocacaoIds")
    int desvincularAlocacoes(@Param("alocacaoIds") Collection<Long> alocacaoIds);
}

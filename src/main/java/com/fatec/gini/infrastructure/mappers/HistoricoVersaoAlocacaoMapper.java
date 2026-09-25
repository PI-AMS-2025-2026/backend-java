package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.HistoricoVersaoAlocacao;
import com.fatec.gini.dto.historicoVersaoAlocacao.HistoricoVersaoAlocacaoRequest;
import com.fatec.gini.dto.historicoVersaoAlocacao.HistoricoVersaoAlocacaoResponse;

public class HistoricoVersaoAlocacaoMapper {
    
    public static HistoricoVersaoAlocacao toEntity(HistoricoVersaoAlocacaoRequest request) {
        if (request == null) {
            return null;
        }
        return new HistoricoVersaoAlocacao(
                request.dataAlteracao(),
                request.justificativa(),
                request.campoAlterado(),
                request.valorAntigo(),
                request.valorNovo());
    }
    
    public static HistoricoVersaoAlocacaoResponse toResponse(HistoricoVersaoAlocacao entity) {
        return new HistoricoVersaoAlocacaoResponse(
                entity.getId(),
                entity.getDataAlteracao(),
                entity.getJustificativa(),
                entity.getCampoAlterado(),
                entity.getValorAntigo(),
                entity.getValorNovo(),
                entity.getAlocacao() != null ? AlocacaoMapper.toResponse(entity.getAlocacao()) : null,
                entity.getUsuario() != null ? UsuarioMapper.toResponse(entity.getUsuario()) : null);
    }
}
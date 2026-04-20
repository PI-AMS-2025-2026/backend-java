package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.HistoricoAlteracao;
import com.fatec.horario.dto.historicoAlteracao.HistoricoAlteracaoRequest;
import com.fatec.horario.dto.historicoAlteracao.HistoricoAlteracaoResponse;

public class HistoricoAlteracaoMapper {
    public static HistoricoAlteracao toEntity(HistoricoAlteracaoRequest request) {
        HistoricoAlteracao entity = new HistoricoAlteracao();
        entity.setDataAlteracao(request.dataAlteracao());
        entity.setJustificativa(request.justificativa());
        entity.setCampoAlterado(request.campoAlterado());
        entity.setValorAntigo(request.valorAntigo());
        entity.setValorNovo(request.valorNovo());

        return entity;
    }

    public static HistoricoAlteracaoResponse toResponse(HistoricoAlteracao entity) {
        return new HistoricoAlteracaoResponse(
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
package com.fatec.gini.dto.alocacao;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AlocacoesGradeRequest(
        List<@NotNull Long> removidas,
        List<@NotNull @Valid AlocacaoAtualizacaoRequest> atualizadas,
        List<@NotNull @Valid AlocacaoRequest> novas) {

    public AlocacoesGradeRequest {
        removidas = removidas == null ? List.of() : removidas;
        atualizadas = atualizadas == null ? List.of() : atualizadas;
        novas = novas == null ? List.of() : novas;
    }

    public record AlocacaoAtualizacaoRequest(@NotNull Long id, @NotNull @Valid AlocacaoRequest alocacao) {}
}

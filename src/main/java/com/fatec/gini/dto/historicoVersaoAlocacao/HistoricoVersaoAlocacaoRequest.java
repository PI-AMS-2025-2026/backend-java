package com.fatec.gini.dto.historicoVersaoAlocacao;

import java.time.LocalDate;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record HistoricoVersaoAlocacaoRequest(
        @NotNull(message = "Data de alteração não pode ser nula") LocalDate dataAlteracao,
        @NotBlank(message = "Justificativa não pode estar vazia") String justificativa,
        @NotBlank(message = "Campo alterado não pode estar vazio") String campoAlterado,
        @NotBlank(message = "Valor antigo não pode estar vazio") String valorAntigo,
        @NotBlank(message = "Valor novo não pode estar vazio") String valorNovo,
        LongDTO alocacao,
        LongDTO usuario) {

}

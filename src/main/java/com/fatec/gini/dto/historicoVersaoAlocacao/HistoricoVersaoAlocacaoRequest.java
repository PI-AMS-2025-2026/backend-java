package com.fatec.gini.dto.historicoVersaoAlocacao;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HistoricoVersaoAlocacaoRequest(

        @NotNull(message = "Data de alteração não pode ser nula")
        LocalDate dataAlteracao,

        @NotBlank(message = "Justificativa não pode estar vazia")
        String justificativa,

        @NotBlank(message = "Campo alterado não pode estar vazio")
        String campoAlterado,

        @NotBlank(message = "Valor antigo não pode estar vazio")
        String valorAntigo,

        @NotBlank(message = "Valor novo não pode estar vazio")
        String valorNovo,

        Long alocacaoId, // ID da alocação para facilitar o envio do payload

        Long usuarioId // ID do usuário responsável pela alteração
) {
}
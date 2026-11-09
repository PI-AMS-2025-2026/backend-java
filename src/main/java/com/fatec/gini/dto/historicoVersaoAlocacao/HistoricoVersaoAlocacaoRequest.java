package com.fatec.gini.dto.historicoVersaoAlocacao;

import java.time.LocalDate;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record HistoricoVersaoAlocacaoRequest(

        @NotNull(message = "Data de alteração não pode ser nula")
        LocalDate dataAlteracao,

        String campoAlterado,

        String valorAntigo,

        String valorNovo,

        LongDTO alocacao, // ID da alocação para facilitar o envio do payload

        LongDTO usuario // ID do usuário responsável pela alteração
) {
}

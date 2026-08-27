package com.fatec.gini.dto.alocacao;

import com.fatec.gini.domain.entities.DiaSemana;

import jakarta.validation.constraints.NotNull;

public record AlocacaoRequest(

    // Alteração: recebe diretamente o ID da entidade para simplificar o payload.
    @NotNull(message = "Turma é obrigatória")
    Long turmaId,

    // Alteração: recebe diretamente o ID da entidade para simplificar o payload.
    @NotNull(message = "Disciplina é obrigatória")
    Long disciplinaId,

    // Alteração: recebe diretamente o ID da entidade para simplificar o payload.
    @NotNull(message = "Sala é obrigatória")
    Long salaId,

    // Alteração: recebe diretamente o ID da entidade para simplificar o payload.
    @NotNull(message = "Professor é obrigatório")
    Long professorId,

    @NotNull(message = "Dia da semana é obrigatório")
    DiaSemana diaSemana,

    // Alteração: recebe diretamente o ID do bloco de horário.
    @NotNull(message = "Horário é obrigatório")
    Long horarioId,

    // Alteração: recebe diretamente o ID do quadro horário.
    @NotNull(message = "Quadro horário é obrigatório")
    Long quadroHorarioId,

    String justificativaAlteracao,

    // Alteração: recebe diretamente o ID do usuário responsável pela alteração.
    @NotNull(message = "Usuário responsável pela alteração é obrigatório")
    Long usuarioAlteracaoId
) {
}
package com.fatec.horario.dto.alocacao;

import com.fatec.horario.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record AlocacaoRequest(

    @NotNull(message = "Turma é obrigatória")
    LongDTO turma,

    @NotNull(message = "Disciplina é obrigatória")
    LongDTO disciplina,

    @NotNull(message = "Sala é obrigatória")
    LongDTO sala,

    @NotNull(message = "Usuário é obrigatório")
    LongDTO usuario,

    @NotNull(message = "Dia da semana é obrigatório")
    LongDTO diaSemana,

    @NotNull(message = "Horário é obrigatório")
    LongDTO horario,

    @NotNull(message = "Grade horária é obrigatória")
    LongDTO gradeHoraria,
    
    //Campos de Histórico de Alteração (opcionais para criação,obrigatorios para registrar mudanças)
    String justificativaAlteracao,

    LongDTO usuarioAlteracao
) {
}
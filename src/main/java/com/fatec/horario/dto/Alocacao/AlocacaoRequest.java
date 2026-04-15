package com.fatec.horario.dto.alocacao;

import com.fatec.horario.domain.entities.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AlocacaoRequest(

    @NotNull(message = "Turma é obrigatória")
    @Valid // Isso faz o Spring validar os campos dentro de Turma
    Turma turma,

    @NotNull(message = "Disciplina é obrigatória")
    @Valid
    Disciplina disciplina,

    @NotNull(message = "Sala é obrigatória")
    @Valid
    Sala sala,

    @NotNull(message = "Usuário é obrigatório")
    @Valid
    Usuario usuario,

    @NotNull(message = "Dia da semana é obrigatório")
    @Valid
    DiaSemana diaSemana,

    @NotNull(message = "Horário é obrigatório")
    @Valid
    Horario horario,

    @NotNull(message = "Grade horária é obrigatória")
    @Valid
    GradeHoraria gradeHoraria
) {
}
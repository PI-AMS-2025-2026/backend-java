package com.fatec.gini.dto.turma;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TurmaRequest(
    @NotNull(message = "O período é obrigatório")
    Integer periodo,

    @NotNull(message = "O ano é obrigatório")
    Integer ano,

    @NotNull(message = "O número de alunos é obrigatório")
    @Positive(message = "O número de alunos deve ser positivo")
    Integer numeroAlunos,

    @NotNull(message = "O curso é obrigatório")
    LongDTO curso
) {}
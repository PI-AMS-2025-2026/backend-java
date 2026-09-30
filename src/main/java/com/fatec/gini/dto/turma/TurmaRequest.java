package com.fatec.gini.dto.turma;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TurmaRequest(

    @NotNull(message = "O período é obrigatório")
    @Positive(message = "O período deve ser positivo")
    Integer periodo,

    @NotNull(message = "O ano é obrigatório")
    @Min(value = 1000, message = "O ano deve possuir 4 dígitos")
    @Max(value = 9999, message = "O ano deve possuir no máximo 4 dígitos")
    Integer ano,

    @NotNull(message = "O número de alunos é obrigatório")
    @Positive(message = "O número de alunos deve ser positivo")
    @Max(value = 50, message = "O número de alunos não pode ser superior a 50")
    Integer numeroAlunos,

    @NotNull(message = "O curso é obrigatório")
    LongDTO curso

) {}
package com.fatec.gini.dto.turma;

import com.fatec.gini.domain.models.Status;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TurmaRequest(

    @NotNull(message = "Período é obrigatório.")
    @Positive(message = "Período deve ser maior que zero.")
    Integer periodo,

    @NotNull(message = "Ano é obrigatório.")
    @Min(value = 2009, message = "Ano da turma deve ser maior que 2008.")
    @Max(value = 9999, message = "Ano deve possuir no máximo 4 dígitos.")
    Integer ano,

    @NotNull(message = "Número de alunos é obrigatório.")
    @Positive(message = "Número de alunos deve ser maior que zero.")
    @Max(value = 50, message = "Número de alunos não pode ser superior a 50.")
    Integer numeroAlunos,

    @NotNull(message = "Curso é obrigatório.")
    LongDTO curso,

    Status status

) {}
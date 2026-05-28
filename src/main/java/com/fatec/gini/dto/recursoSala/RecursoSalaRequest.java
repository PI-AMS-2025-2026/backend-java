package com.fatec.gini.dto.recursoSala;

import jakarta.validation.constraints.NotNull;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.Min;

public record RecursoSalaRequest(

    @NotNull
    LongDTO sala,

    @NotNull
    LongDTO recurso,

    @NotNull
    @Min(value = 1, message = "Quantidade deve ser maior que zero.")
    Integer quantidade

) {}
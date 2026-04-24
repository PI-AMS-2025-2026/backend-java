package com.fatec.horario.dto.recursoSala;

import com.fatec.horario.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;
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
package com.fatec.horario.dto.RecursoSala;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecursoSalaRequest(

    @NotNull
    Long salaId,

    @NotNull
    Long recursoId,

    @NotNull
    @Positive(message = "Quantidade deve ser maior que zero")
    Integer quantidade

) {}

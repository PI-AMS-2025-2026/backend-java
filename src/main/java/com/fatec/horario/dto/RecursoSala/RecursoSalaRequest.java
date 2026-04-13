package com.fatec.horario.dto.RecursoSala;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record RecursoSalaRequest(

    @NotNull
    Long salaId,

    @NotNull
    Long recursoId,

    @NotNull
    @Min(value = 1, message = "Quantidade deve ser maior que zero.")
    Integer quantidade

) {}
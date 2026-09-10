package com.fatec.gini.dto.recursoSala;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RecursoSalaRequest(

    // ALTERAÇÃO: recebe diretamente o ID da sala, facilitando o payload.
    @NotNull(message = "Sala é obrigatória.")
    Long salaId,

    // ALTERAÇÃO: recebe diretamente o ID do recurso, sem precisar de {"id": ...}.
    @NotNull(message = "Recurso é obrigatório.")
    Long recursoId,

    @NotNull
    @Min(value = 1, message = "Quantidade deve ser maior que zero.")
    Integer quantidade

) {}

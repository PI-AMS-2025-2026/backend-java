package com.fatec.gini.dto.recursoSala;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RecursoSalaRequest(

    // ALTERAÇÃO: recebe diretamente o ID da sala, facilitando o payload.
    @NotNull(message = "SalaId é obrigatório.")
    LongDTO sala,

    // ALTERAÇÃO: recebe diretamente o ID do recurso, sem precisar de {"id": ...}.
    @NotNull(message = "Recurso é obrigatório.")
    LongDTO recurso,
   // ALTERAÇÃO: validação para garantir que a quantidade seja maior que zero.
     @NotNull(message = "Quantidade é obrigatória.")
     @Min(value = 1, message = "O campo quantidade deve ser maior que zero.")
     Integer quantidade
) {}
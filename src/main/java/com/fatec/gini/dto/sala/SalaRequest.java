package com.fatec.gini.dto.sala;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SalaRequest(

        @NotBlank(message = "O código é obrigatório")
        @Size(max = 20, message = "O código deve ter no máximo 20 caracteres")
        String codigo,

        @NotNull(message = "A capacidade é obrigatória")
        @Positive(message = "A capacidade deve ser um número positivo")
        Integer capacidade,

        // ALTERAÇÃO: recebe somente o ID do tipo de sala para simplificar o payload.
        @NotNull(message = "O tipo de sala é obrigatório")
        Long tipoSalaId

) {}
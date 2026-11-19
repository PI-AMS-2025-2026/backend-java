package com.fatec.gini.dto.sala;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SalaRequest(

        @NotBlank(message = "Código é obrigatório.")
        @Size(max = 20, message = "Código deve ter no máximo 20 caracteres.")
        String codigo,

        @NotNull(message = "Capacidade é obrigatória.")
        @Positive(message = "Capacidade deve ser um número positivo.")
        Integer capacidade,

        // ALTERAÇÃO: recebe somente o ID do tipo de sala para simplificar o payload.
        @NotNull(message = "Tipo de sala é obrigatório.")
        LongDTO tipoSala

) {}
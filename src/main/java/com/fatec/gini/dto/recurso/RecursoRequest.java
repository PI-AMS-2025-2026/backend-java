package com.fatec.gini.dto.recurso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecursoRequest(
        @NotBlank(message = "Nome é Obrigatorio.")
        @Size(min = 2, max = 100, message = "O nome deve ter no minimo 2 e no maximo 100 caracteres.")
        String nome,
        // ALTERAÇÃO: recebe diretamente o ID do tipo de recurso no payload.
        @NotNull(message = "Tipo recurso é obrigatorio.")
        Long tipoRecurso
        ) {

}

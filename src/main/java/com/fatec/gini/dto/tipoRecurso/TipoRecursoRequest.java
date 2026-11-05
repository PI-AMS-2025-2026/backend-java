package com.fatec.gini.dto.tipoRecurso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoRecursoRequest(
        @NotBlank(message = "Nome é Obrigatorio.") 
        @Size(min = 2, max = 100, message = "O nome deve ter no minimo 2 e no maximo 100 caracteres.") 
        String nome
) {
}

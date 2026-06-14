package com.fatec.gini.dto.recurso;

import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecursoRequest(

        @NotBlank(message = "Nome é Obrigatorio.") 
        @Size(min = 2, max = 100, message = "O nome deve ter no minimo 2 e no maximo 100 caracteres.") 
        String nome,

        @NotBlank(message = "Tipo recurso é obrigatorio.") 
        LongDTO tipoRecurso

) {
}
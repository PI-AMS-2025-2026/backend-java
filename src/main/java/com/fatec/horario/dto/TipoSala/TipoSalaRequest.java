package com.fatec.horario.dto.tipoSala;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoSalaRequest(

        @NotBlank(message = "Nome é Obrigatorio.") @Size(min = 2, max = 100, message = "O nome deve ter no minimo 2 e no maximo 100 caracteres.") String nome

// nome não pode ser vazio
// tipo não pode ser vazio (Recurso)

) {
}

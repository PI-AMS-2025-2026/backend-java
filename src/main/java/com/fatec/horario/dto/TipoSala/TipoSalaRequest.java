package com.fatec.horario.dto.TipoSala;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoSalaRequest(

    @NotBlank (message = "Nome é Obrigatorio.")
    @Size (min = 5, max = 100, message = "O nome deve ter no minimo 5 e no maximo 100 caracteres.")
    String name

//nome não pode ser vazio
//tipo não pode ser vazio (Recurso)

) {
}

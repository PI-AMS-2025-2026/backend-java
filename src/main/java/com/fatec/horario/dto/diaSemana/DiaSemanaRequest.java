package com.fatec.horario.dto.diaSemana;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DiaSemanaRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 20, message = "Nome deve ter entre 3 e 20 caracteres")
    String nome
) {
} 
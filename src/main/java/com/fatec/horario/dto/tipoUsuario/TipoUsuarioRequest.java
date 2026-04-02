package com.fatec.horario.dto.tipoUsuario;

import jakarta.validation.constraints.NotBlank;

public record TipoUsuarioRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome

) {}
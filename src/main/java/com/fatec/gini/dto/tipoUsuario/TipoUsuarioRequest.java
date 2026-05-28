package com.fatec.gini.dto.tipoUsuario;

import jakarta.validation.constraints.NotBlank;

public record TipoUsuarioRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome

) {}
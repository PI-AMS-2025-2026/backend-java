package com.fatec.horario.dto.usuario;

import jakarta.validation.constraints.*;

public record UsuarioRequest(

        @NotBlank
        String nome,

        @Email
        @NotBlank
        String email,

        @Size(min = 6)
        String senha,

        @NotBlank
        String cidade,

        @NotNull
        Boolean status,

        @NotNull
        Long id_tipo_usuario
) {}
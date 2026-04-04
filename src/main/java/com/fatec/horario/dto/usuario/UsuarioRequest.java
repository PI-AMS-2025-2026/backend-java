package com.fatec.horario.dto.usuario;

import com.fatec.horario.dto.id.LongDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


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
        String status,

        @NotNull
        LongDTO tipoUsuario
) {}
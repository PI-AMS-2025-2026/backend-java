package com.fatec.horario.dto.usuario;

import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.dto.id.LongDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UsuarioRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @Email(message = "E-mail deve ter um formato válido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        @NotBlank(message = "Cidade é obrigatória")
        String cidade,

        @NotNull(message = "Status é obrigatório")
        Status status,

        @NotNull(message = "Tipo de usuário é obrigatório")
        LongDTO tipoUsuario
) {}
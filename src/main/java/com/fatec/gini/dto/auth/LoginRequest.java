package com.fatec.gini.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "E-mail deve ter um formato válido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,
        @NotBlank(message = "Senha é obrigatório")
        String senha) {

}

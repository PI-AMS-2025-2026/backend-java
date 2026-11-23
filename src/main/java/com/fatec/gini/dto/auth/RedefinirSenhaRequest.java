package com.fatec.gini.dto.auth;

import com.fatec.gini.web.validation.SenhaForte;

import jakarta.validation.constraints.NotBlank;

public record RedefinirSenhaRequest(
        @NotBlank(message = "Token é obrigatório")
        String token,
        @NotBlank(message = "Senha é obrigatória")
        @SenhaForte
        String senha) {
}

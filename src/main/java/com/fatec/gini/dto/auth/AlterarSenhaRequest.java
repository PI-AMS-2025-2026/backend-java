package com.fatec.gini.dto.auth;

import com.fatec.gini.web.validation.SenhaForte;

import jakarta.validation.constraints.NotBlank;

public record AlterarSenhaRequest(
        @NotBlank(message = "Senha atual é obrigatória.") String senhaAtual,
        @NotBlank(message = "Nova senha é obrigatória.") @SenhaForte String novaSenha) {
}

package com.fatec.gini.dto.login;

import jakarta.validation.constraints.Email;

public record LoginRequest(
    @Email
    String email,
    String senha
) {

}

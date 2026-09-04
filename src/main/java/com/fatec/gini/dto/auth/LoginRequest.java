package com.fatec.gini.dto.auth;

import jakarta.validation.constraints.Email;

public record LoginRequest(
        @Email String email,
        String senha) {

}

package com.fatec.horario.dto.auth;

public record LoginResponse(
        String tokenType,
        String accessToken,
        long expiresIn,
        String role) {
}

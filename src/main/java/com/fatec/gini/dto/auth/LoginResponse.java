package com.fatec.gini.dto.auth;

public record LoginResponse(
                String accessToken,
                String refreshToken) {
}
package com.fatec.gini.dto.auth;

import com.fatec.gini.dto.usuario.UsuarioResponse;

public record LoginResponse(
                String accessToken,
                String refreshToken,
                UsuarioResponse usuario) {
}

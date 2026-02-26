package com.fatec.horario.dto.User;

import com.fatec.horario.dto.AcessLevel.AccessLevelResponse;

public record UserResponse(
        Long id,
        String name,
        String email,
        String password,
        AccessLevelResponse accessLevel) {
}

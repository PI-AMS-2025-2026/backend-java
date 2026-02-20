package com.fatec.horario.dto;

public record UserResponse(
        Long id,
        String name,
        String email,
        String password,
        AccessLevelResponse accessLevel) {
}

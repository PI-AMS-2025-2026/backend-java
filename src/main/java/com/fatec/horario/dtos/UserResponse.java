package com.fatec.horario.dtos;

public record UserResponse(
    Long id,
    String name,
    String email,
    String password,
    AccessLevelResponse accessLevel
) {}

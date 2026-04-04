package com.fatec.horario.dto.usuario;

import java.time.LocalDateTime;

import com.fatec.horario.dto.tipoUsuario.TipoUsuarioResponse;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        TipoUsuarioResponse tipoUsuario,
        String cidade,
        String status,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {}
package com.fatec.horario.dto.usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Boolean status,
        LocalDateTime created_at,
        Long tipo_usuario_id
) {}
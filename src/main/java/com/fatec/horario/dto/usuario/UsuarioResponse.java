package com.fatec.horario.dto.usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String cidade,
        Boolean status,
        LocalDateTime created_at,
        Long tipo_usuario_id
) {}
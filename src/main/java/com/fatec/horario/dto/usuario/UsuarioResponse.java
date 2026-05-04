package com.fatec.horario.dto.usuario;

import java.time.LocalDateTime;

import com.fatec.horario.domain.entities.Status;
import com.fatec.horario.dto.curso.CursoResponse;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioResponse;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        TipoUsuarioResponse tipoUsuario,
        String cidade,
        Status status,
        CursoResponse curso,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {}
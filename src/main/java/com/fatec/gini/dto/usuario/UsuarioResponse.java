package com.fatec.gini.dto.usuario;

import java.time.LocalDateTime;

import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.dto.curso.CursoResponse;
import com.fatec.gini.dto.tipoUsuario.TipoUsuarioResponse;

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
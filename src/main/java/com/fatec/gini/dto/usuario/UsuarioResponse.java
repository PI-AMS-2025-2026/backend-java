package com.fatec.gini.dto.usuario;

import java.time.LocalDateTime;

import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.entities.TipoUsuario;
import com.fatec.gini.dto.curso.CursoResponse;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        TipoUsuario tipoUsuario,
        Status status,
        CursoResponse curso,
        LocalDateTime created_at,
        LocalDateTime updated_at
) {}
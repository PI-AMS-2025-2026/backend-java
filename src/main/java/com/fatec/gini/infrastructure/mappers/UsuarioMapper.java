package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.dto.usuario.UsuarioRequest;
import com.fatec.gini.dto.usuario.UsuarioResponse;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequest request) {
        if (request == null) {
            return null;
        }
        return new Usuario(
                request.nome(),
                request.email(),
                request.senha(),
                request.cidade(),
                request.status());
    }

    public static UsuarioResponse toResponse(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo_usuario() != null ? TipoUsuarioMapper.toResponse(usuario.getTipo_usuario()) : null,
                usuario.getCidade(),
                usuario.getStatus(),
                usuario.getCurso() != null ? CursoMapper.toResponse(usuario.getCurso()) : null,
                usuario.getCreated_at(),
                usuario.getUpdated_at());
    }
}
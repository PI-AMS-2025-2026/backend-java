package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.user.Usuario;
import com.fatec.gini.dto.usuario.UsuarioRequest;
import com.fatec.gini.dto.usuario.UsuarioResponse;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequest request) {
        if (request == null) {
            return null;
        }
        return new Usuario(request.nome(), request.email(), request.senha(), request.status(),request.tipoUsuario()) ;
    }

    public static UsuarioResponse toResponse(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getStatus(),
                usuario.getCurso() != null ? CursoMapper.toResponse(usuario.getCurso()) : null,
                usuario.getCreatedAt(),
                usuario.getUpdatedAt());
    }
}

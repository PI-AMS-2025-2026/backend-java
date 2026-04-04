package com.fatec.horario.infrastructure.mappers;

import org.springframework.beans.BeanUtils;

import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.dto.usuario.UsuarioRequest;
import com.fatec.horario.dto.usuario.UsuarioResponse;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(request, usuario);

        return usuario;
    }

    public static UsuarioResponse toResponse(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo_usuario() != null ? TipoUsuarioMapper.toResponse(usuario.getTipo_usuario()) : null,
                usuario.getCidade(),
                usuario.getStatus(),
                usuario.getCreated_at(),
                usuario.getUpdated_at());
    }
}
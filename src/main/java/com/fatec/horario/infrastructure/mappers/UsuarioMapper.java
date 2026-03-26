package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.TipoUsuario;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.dto.usuario.UsuarioRequest;
import com.fatec.horario.dto.usuario.UsuarioResponse;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequest dto, TipoUsuario tipo) {
        Usuario u = new Usuario();
        u.setNome(dto.nome());
        u.setEmail(dto.email());
        u.setSenha(dto.senha());
        u.setCidade(dto.cidade());
        u.setStatus(dto.status());
        u.setTipo_usuario(tipo);
        return u;
    }

    public static UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId_usuario(),
                u.getNome(),
                u.getEmail(),
                u.getCidade(),
                u.getStatus(),
                u.getCreated_at(),
                u.getTipo_usuario().getId_tipo_usuario()
        );
    }
}
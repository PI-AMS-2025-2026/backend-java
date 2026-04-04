package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.TipoUsuario;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioRequest;
import com.fatec.horario.dto.tipoUsuario.TipoUsuarioResponse;

public class TipoUsuarioMapper {

    public static TipoUsuario toEntity(TipoUsuarioRequest dto) {
        TipoUsuario tipo = new TipoUsuario();
        tipo.setNome(dto.nome());
        return tipo;
    }

    public static TipoUsuarioResponse toResponse(TipoUsuario tipo) {
        return new TipoUsuarioResponse(
                tipo.getId(),
                tipo.getNome()
        );
    }
}
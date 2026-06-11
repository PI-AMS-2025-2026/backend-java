package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.dto.alocacao.AlocacaoRequest;
import com.fatec.gini.dto.alocacao.AlocacaoResponse;

public class AlocacaoMapper {

    // Request -> Entity
    public static Alocacao toEntity(AlocacaoRequest request) {

        if (request == null) {
            return null;
        }

        Alocacao entity = new Alocacao();

        return entity;
    }

    // Entity -> Response
    public static AlocacaoResponse toResponse(Alocacao entity) {

        if (entity == null) {
            return null;
        }

        return new AlocacaoResponse(
                entity.getId(),
                entity.getTurma() != null
                        ? TurmaMapper.toResponse(entity.getTurma())
                        : null,
                entity.getDisciplina() != null
                        ? DisciplinaMapper.toResponse(entity.getDisciplina())
                        : null,
                entity.getSala() != null
                        ? SalaMapper.toResponse(entity.getSala())
                        : null,
                entity.getUsuario() != null
                        ? UsuarioMapper.toResponse(entity.getUsuario())
                        : null,
                entity.getDiaSemana() != null
                        ? DiaSemanaMapper.toResponse(entity.getDiaSemana())
                        : null,
                entity.getHorario() != null
                        ? HorarioMapper.toResponse(entity.getHorario())
                        : null,
                entity.getGradeHoraria() != null
                        ? GradeHorariaMapper.toResponse(entity.getGradeHoraria())
                        : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
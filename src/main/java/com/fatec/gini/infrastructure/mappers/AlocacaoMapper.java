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
        return new Alocacao();
    }

    // Entity -> Response
    public static AlocacaoResponse toResponse(Alocacao entity) {
        return new AlocacaoResponse(
                entity.getId(),
                entity.getTurma() != null ? TurmaMapper.toResponse(entity.getTurma()) : null,
                entity.getDisciplina() != null ? DisciplinaMapper.toResponse(entity.getDisciplina()) : null,
                entity.getSala() != null ? SalaMapper.toResponse(entity.getSala()) : null,
                entity.getProfessor() != null ? ProfessorMapper.toResponse(entity.getProfessor()) : null,
                entity.getDiaSemana() != null ? DiaSemanaMapper.toResponse(entity.getDiaSemana()) : null,
                entity.getBlocoHorario() != null ? BlocoHorarioMapper.toResponse(entity.getBlocoHorario()) : null,
                entity.getGradeHoraria() != null ? GradeHorariaMapper.toResponse(entity.getGradeHoraria()) : null);
    }
}
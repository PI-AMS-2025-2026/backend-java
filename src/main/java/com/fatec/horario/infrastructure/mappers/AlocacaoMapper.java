package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.dto.alocacao.AlocacaoRequest;
import com.fatec.horario.dto.alocacao.AlocacaoResponse;

public class AlocacaoMapper {

    // Request -> Entity
    public static Alocacao toEntity(AlocacaoRequest dto) {
        if (dto == null) {
            return null;
        }

        Alocacao alocacao = new Alocacao();
        
        alocacao.setTurma(dto.turma());
        alocacao.setDisciplina(dto.disciplina());
        alocacao.setSala(dto.sala());
        alocacao.setUsuario(dto.usuario());
        alocacao.setDiaSemana(dto.diaSemana());
        alocacao.setHorario(dto.horario());
        alocacao.setGradeHoraria(dto.gradeHoraria());
        
        return alocacao;
    }

    // Entity -> Response
    public static AlocacaoResponse toResponse(Alocacao alocacao) {
        if (alocacao == null) {
            return null;
        }

        return new AlocacaoResponse(
                alocacao.getId(),
                alocacao.getTurma(),
                alocacao.getDisciplina(),
                alocacao.getSala(),
                alocacao.getUsuario(),
                alocacao.getDiaSemana(),
                alocacao.getHorario(),
                alocacao.getGradeHoraria()
        );
    }
}
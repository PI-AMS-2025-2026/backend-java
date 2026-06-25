package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.dto.alocacao.AlocacaoRequest;
import com.fatec.gini.dto.alocacao.AlocacaoResponse;

public class AlocacaoMapper {

    // Request -> Entity
    public static Alocacao toEntity(AlocacaoRequest request) {
        if (request == null) {
            return null;
        }

        Alocacao entity = new Alocacao();

        if (request.turma() != null) {
            Turma turma = new Turma();
            turma.setId(request.turma().id());
            entity.setTurma(turma);
        }

        if (request.disciplina() != null) {
            Disciplina disciplina = new Disciplina();
            disciplina.setId(request.disciplina().id());
            entity.setDisciplina(disciplina);
        }

        if (request.sala() != null) {
            Sala sala = new Sala();
            sala.setId(request.sala().id());
            entity.setSala(sala);
        }

        if (request.professor() != null) {
            Professor professor = new Professor();
            professor.setId(request.professor().id());
            entity.setProfessor(professor);
        }

        entity.setDiaSemana(request.diaSemana());

        if (request.horario() != null) {
            BlocoHorario blocoHorario = new BlocoHorario();
            blocoHorario.setId(request.horario().id());
            entity.setBlocoHorario(blocoHorario);
        }

        if (request.quadroHorario() != null) {
            QuadroHorario quadroHorario = new QuadroHorario();
            quadroHorario.setId(request.quadroHorario().id());
            entity.setQuadroHorario(quadroHorario);
        }

        return entity;
    }

    // Entity -> Response
    public static AlocacaoResponse toResponse(Alocacao entity) {
        return new AlocacaoResponse(
                entity.getId(),
                entity.getTurma() != null ? TurmaMapper.toResponse(entity.getTurma()) : null,
                entity.getDisciplina() != null ? DisciplinaMapper.toResponse(entity.getDisciplina()) : null,
                entity.getSala() != null ? SalaMapper.toResponse(entity.getSala()) : null,
                entity.getProfessor() != null ? ProfessorMapper.toResponse(entity.getProfessor()) : null,
                entity.getDiaSemana(),
                entity.getBlocoHorario() != null ? BlocoHorarioMapper.toResponse(entity.getBlocoHorario()) : null,
                entity.getQuadroHorario() != null ? QuadroHorarioMapper.toResponse(entity.getQuadroHorario()) : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
                );
    }
}
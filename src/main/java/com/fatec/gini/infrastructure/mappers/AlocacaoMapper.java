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

        // Alteração: utiliza diretamente o ID recebido no payload.
        if (request.turmaId() != null) {
            Turma turma = new Turma();
            turma.setId(request.turmaId());
            entity.setTurma(turma);
        }

        // Alteração: utiliza diretamente o ID recebido no payload.
        if (request.disciplinaId() != null) {
            Disciplina disciplina = new Disciplina();
            disciplina.setId(request.disciplinaId());
            entity.setDisciplina(disciplina);
        }

        // Alteração: utiliza diretamente o ID recebido no payload.
        if (request.salaId() != null) {
            Sala sala = new Sala();
            sala.setId(request.salaId());
            entity.setSala(sala);
        }

        // Alteração: utiliza diretamente o ID recebido no payload.
        if (request.professorId() != null) {
            Professor professor = new Professor();
            professor.setId(request.professorId());
            entity.setProfessor(professor);
        }

        entity.setDiaSemana(request.diaSemana());

        // Alteração: utiliza diretamente o ID do horário recebido no payload.
        if (request.horarioId() != null) {
            BlocoHorario blocoHorario = new BlocoHorario();
            blocoHorario.setId(request.horarioId());
            entity.setBlocoHorario(blocoHorario);
        }

        // Alteração: utiliza diretamente o ID do quadro horário recebido no payload.
        if (request.quadroHorarioId() != null) {
            QuadroHorario quadroHorario = new QuadroHorario();
            quadroHorario.setId(request.quadroHorarioId());
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
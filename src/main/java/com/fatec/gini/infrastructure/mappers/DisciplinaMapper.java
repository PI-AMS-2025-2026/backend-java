package com.fatec.gini.infrastructure.mappers;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.TipoSala;
import com.fatec.gini.dto.disciplina.DisciplinaRequest;
import com.fatec.gini.dto.disciplina.DisciplinaResponse;

public class DisciplinaMapper {

    public static Disciplina toEntity(DisciplinaRequest request) {
        if (request == null) {
            return null;
        }

        Disciplina entity = new Disciplina();

        entity.setNome(request.nome());
        entity.setCargaHoraria(request.cargaHoraria());
        entity.setTipoDisciplina(request.tipoDisciplina());
        entity.setPeriodo(request.periodo());
        entity.setModalidade(request.modalidade());
        entity.setCodDisciplina(request.codDisciplina());
        entity.setCor(request.cor());

        if (request.curso() != null) {
            Curso curso = new Curso();
            curso.setId(request.curso().id());
            entity.setCurso(curso);
        }

        if (request.tipoSala() != null) {
            TipoSala tipoSala = new TipoSala();
            tipoSala.setId(request.tipoSala().id());
            entity.setTipoSala(tipoSala);
        }

        return entity;
    }

    public static DisciplinaResponse toResponse(Disciplina entity) {
        if (entity == null) {
            return null;
        }

        return new DisciplinaResponse(
                entity.getId(),
                entity.getNome(),
                entity.getCargaHoraria(),
                entity.getTipoDisciplina(),
                entity.getPeriodo(),
                entity.getModalidade(),
                entity.getCodDisciplina(),
                entity.getCor(),
                entity.getCurso() != null ? CursoMapper.toResponse(entity.getCurso()) : null,
                entity.getTipoSala() != null ? TipoSalaMapper.toResponse(entity.getTipoSala()) : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
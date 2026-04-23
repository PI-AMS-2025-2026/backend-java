package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.dto.disciplina.DisciplinaRequest;
import com.fatec.horario.dto.disciplina.DisciplinaResponse;

public class DisciplinaMapper {

    public static Disciplina toEntity(DisciplinaRequest request) {

        Disciplina disciplina = new Disciplina();

        disciplina.setNome(request.nome());
        disciplina.setCargaHoraria(request.cargaHoraria());
        disciplina.setTipoDisciplina(request.tipoDisciplina());
        disciplina.setPeriodo(request.periodo());
        disciplina.setModalidade(request.modalidade());
        disciplina.setCodDisciplina(request.codDisciplina());
        disciplina.setCor(request.cor());

        return disciplina;
    }

    public static DisciplinaResponse toResponse(Disciplina disciplina) {

        return new DisciplinaResponse(
                disciplina.getId(),
                disciplina.getNome(),
                disciplina.getCargaHoraria(),
                disciplina.getTipoDisciplina(),
                disciplina.getPeriodo(),
                disciplina.getModalidade(),
                disciplina.getCodDisciplina(),
                disciplina.getCor(),
                disciplina.getCurso() != null ? CursoMapper.toResponse(disciplina.getCurso()) : null,
                disciplina.getTipoSala() != null ? TipoSalaMapper.toResponse(disciplina.getTipoSala()) : null
        );
    }
}
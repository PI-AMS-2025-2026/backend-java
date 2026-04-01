package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.domain.entities.TipoSala;
import com.fatec.horario.dto.Disciplina.DisciplinaRequest;
import com.fatec.horario.dto.Disciplina.DisciplinaResponse;

public class DisciplinaMapper {

    public static Disciplina toEntity(DisciplinaRequest request) {
        Disciplina disciplina = new Disciplina();
        disciplina.setNome(request.getNome());
        disciplina.setCargaHoraria(request.getCargaHoraria());
        disciplina.setTipoDisciplina(request.getTipoDisciplina());
        disciplina.setPeriodo(request.getPeriodo());
        disciplina.setModalidade(request.getModalidade());
        disciplina.setCodDisciplina(request.getCodDisciplina());
        disciplina.setCor(request.getCor());
        if (request.getIdCurso() != null) {
            Curso curso = new Curso();
            curso.setIdCurso(request.getIdCurso());
            disciplina.setCurso(curso);
        }

        if (request.getIdTipoSala() != null) {
            TipoSala tipoSala = new TipoSala();
            tipoSala.setIdTipoSala(request.getIdTipoSala());
            disciplina.setTipoSala(tipoSala);
        }

        return disciplina;
    }

    public static DisciplinaResponse toResponse(Disciplina disciplina) {
        return new DisciplinaResponse(
                disciplina.getIdDisciplina(),
                disciplina.getNome(),
                disciplina.getCargaHoraria(),
                disciplina.getTipoDisciplina(),
                disciplina.getPeriodo(),
                disciplina.getModalidade(),
                disciplina.getCodDisciplina(),
                disciplina.getCor(),
                disciplina.getCurso() != null ? disciplina.getCurso().getIdCurso() : null,
                disciplina.getTipoSala() != null ? disciplina.getTipoSala().getIdTipoSala() : null
        );
    }
    
}

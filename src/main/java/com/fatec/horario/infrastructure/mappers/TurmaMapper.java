package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.dto.Turma.TurmaRequest;
import com.fatec.horario.dto.Turma.TurmaResponse;

public class TurmaMapper {

    public static Turma toEntity(TurmaRequest request) {

        Turma turma = new Turma();

        turma.setCodigo(request.codigo());
        turma.setPeriodo(request.periodo());
        turma.setAno(request.ano());
        turma.setNumeroAlunos(request.numeroAlunos());

        // 🔥 Mantém só o ID (o Service valida e busca no banco)
        Curso curso = new Curso();
        curso.setId(request.idCurso());

        turma.setCurso(curso);

        return turma;
    }

    public static TurmaResponse toResponse(Turma turma) {

        return new TurmaResponse(
                turma.getIdTurma(),
                turma.getCodigo(),
                turma.getPeriodo(),
                turma.getAno(),
                turma.getNumeroAlunos(),
                turma.getCurso().getId()
        );
    }
}

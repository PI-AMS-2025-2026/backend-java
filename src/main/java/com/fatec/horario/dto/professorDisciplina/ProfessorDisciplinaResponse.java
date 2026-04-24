package com.fatec.horario.dto.professorDisciplina;

import com.fatec.horario.dto.disciplina.DisciplinaResponse;
import com.fatec.horario.dto.usuario.UsuarioResponse;

public record ProfessorDisciplinaResponse(
        Long id,
        UsuarioResponse usuario,
        DisciplinaResponse disciplina
) {
}

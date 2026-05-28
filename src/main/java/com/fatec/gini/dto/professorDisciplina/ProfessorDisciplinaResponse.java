package com.fatec.gini.dto.professorDisciplina;

import com.fatec.gini.dto.disciplina.DisciplinaResponse;
import com.fatec.gini.dto.usuario.UsuarioResponse;

public record ProfessorDisciplinaResponse(
        Long id,
        UsuarioResponse usuario,
        DisciplinaResponse disciplina
) {
}

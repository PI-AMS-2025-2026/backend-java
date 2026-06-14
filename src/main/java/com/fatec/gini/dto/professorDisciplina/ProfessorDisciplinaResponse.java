package com.fatec.gini.dto.professorDisciplina;

import com.fatec.gini.dto.disciplina.DisciplinaResponse;
import com.fatec.gini.dto.professor.ProfessorResponse;

public record ProfessorDisciplinaResponse(
        Long id,
        ProfessorResponse professor,
        DisciplinaResponse disciplina
) {
}

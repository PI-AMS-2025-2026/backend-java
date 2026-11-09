
package com.fatec.gini.dto.professorDisciplina;

import com.fatec.gini.dto.professor.ProfessorResponse;
import com.fatec.gini.dto.disciplina.DisciplinaResponse;

public record ProfessorDisciplinaResponse(
        Long id,
        Long professorId,
        Long disciplinaId,
        ProfessorResponse professor,
        DisciplinaResponse disciplina
) {
}

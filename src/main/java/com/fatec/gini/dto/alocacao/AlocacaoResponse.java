package com.fatec.gini.dto.alocacao;

import com.fatec.gini.dto.blocoHorario.BlocoHorarioResponse;
import com.fatec.gini.dto.diaSemana.DiaSemanaResponse;
import com.fatec.gini.dto.disciplina.DisciplinaResponse;
import com.fatec.gini.dto.gradeHoraria.GradeHorariaResponse;
import com.fatec.gini.dto.professor.ProfessorResponse;
import com.fatec.gini.dto.sala.SalaResponse;
import com.fatec.gini.dto.turma.TurmaResponse;

public record AlocacaoResponse(
        Long id,
        TurmaResponse turma,
        DisciplinaResponse disciplina,
        SalaResponse sala,
        ProfessorResponse professor,
        DiaSemanaResponse diaSemana,
        BlocoHorarioResponse blocoHorario,
        GradeHorariaResponse gradeHoraria) {
}
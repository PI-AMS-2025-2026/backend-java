package com.fatec.gini.dto.alocacao;

import com.fatec.gini.dto.diaSemana.DiaSemanaResponse;
import com.fatec.gini.dto.disciplina.DisciplinaResponse;
import com.fatec.gini.dto.gradeHoraria.GradeHorariaResponse;
import com.fatec.gini.dto.horarios.HorarioResponse;
import com.fatec.gini.dto.sala.SalaResponse;
import com.fatec.gini.dto.turma.TurmaResponse;
import com.fatec.gini.dto.usuario.UsuarioResponse;

public record AlocacaoResponse(
        Long id,
        TurmaResponse turma,
        DisciplinaResponse disciplina,
        SalaResponse sala,
        UsuarioResponse usuario,
        DiaSemanaResponse diaSemana,
        HorarioResponse horario,
        GradeHorariaResponse gradeHoraria) {
}
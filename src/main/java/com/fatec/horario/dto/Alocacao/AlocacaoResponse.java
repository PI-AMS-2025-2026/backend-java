package com.fatec.horario.dto.alocacao;

import com.fatec.horario.dto.diaSemana.DiaSemanaResponse;
import com.fatec.horario.dto.disciplina.DisciplinaResponse;
import com.fatec.horario.dto.gradeHoraria.GradeHorariaResponse;
import com.fatec.horario.dto.horarios.HorarioResponse;
import com.fatec.horario.dto.sala.SalaResponse;
import com.fatec.horario.dto.turma.TurmaResponse;
import com.fatec.horario.dto.usuario.UsuarioResponse;

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
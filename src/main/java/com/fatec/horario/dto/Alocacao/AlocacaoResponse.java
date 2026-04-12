package com.fatec.horario.dto.Alocacao;

import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.domain.entities.Usuario;

public record AlocacaoResponse(
        Long id,
        Turma turma,
        Disciplina disciplina,
        Sala sala,
        Usuario usuario,
        DiaSemana diaSemana,
        Horario horario,
        GradeHoraria gradehoraria) {
}
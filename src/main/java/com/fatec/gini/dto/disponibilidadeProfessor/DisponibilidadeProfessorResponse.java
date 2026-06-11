package com.fatec.gini.dto.disponibilidadeProfessor;

import com.fatec.gini.dto.blocoHorario.BlocoHorarioResponse;
import com.fatec.gini.dto.diaSemana.DiaSemanaResponse;
import com.fatec.gini.dto.professor.ProfessorResponse;

public record DisponibilidadeProfessorResponse(
    Long id,
    ProfessorResponse usuario,
    DiaSemanaResponse diaSemana,
    BlocoHorarioResponse blocoHorario
) {
    
}

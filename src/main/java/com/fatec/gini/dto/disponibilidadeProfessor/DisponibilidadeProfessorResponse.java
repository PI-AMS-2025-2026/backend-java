package com.fatec.gini.dto.disponibilidadeProfessor;

import com.fatec.gini.dto.diaSemana.DiaSemanaResponse;
import com.fatec.gini.dto.horarios.HorarioResponse;
import com.fatec.gini.dto.usuario.UsuarioResponse;

public record DisponibilidadeProfessorResponse(
    Long id,
    UsuarioResponse usuario,
    DiaSemanaResponse diaSemana,
    HorarioResponse horario
) {
    
}

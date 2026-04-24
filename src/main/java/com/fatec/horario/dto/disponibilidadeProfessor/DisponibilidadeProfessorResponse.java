package com.fatec.horario.dto.disponibilidadeProfessor;

import com.fatec.horario.dto.diaSemana.DiaSemanaResponse;
import com.fatec.horario.dto.horarios.HorarioResponse;
import com.fatec.horario.dto.usuario.UsuarioResponse;

public record DisponibilidadeProfessorResponse(
    Long id,
    UsuarioResponse usuario,
    DiaSemanaResponse diaSemana,
    HorarioResponse horario
) {
    
}

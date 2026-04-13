package com.fatec.horario.dto.DisponibilidadeProfessor;

public record DisponibilidadeProfessorResponse(
    Long id,
    Long idUsuario,
    Long idDiaSemana,
    Long idHorario
) {
    
}

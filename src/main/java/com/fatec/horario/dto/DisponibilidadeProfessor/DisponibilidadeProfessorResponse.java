package com.fatec.horario.dto.disponibilidadeProfessor;

public record DisponibilidadeProfessorResponse(
    Long id,
    Long idUsuario,
    Long idDiaSemana,
    Long idHorario
) {
    
}

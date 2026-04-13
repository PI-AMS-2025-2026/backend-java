package com.fatec.horario.dto.recursoSala;

public record RecursoSalaResponse(
    Long id,
    Long salaId,
    Long recursoId,
    Integer quantidade
) {}
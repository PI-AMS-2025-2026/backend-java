package com.fatec.horario.dto.Sala;

public record SalaResponse(
        Long id,
        String codigo,
        Integer capacidade,
        Long idTipoSala
) {}
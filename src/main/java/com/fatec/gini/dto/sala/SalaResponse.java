package com.fatec.gini.dto.sala;

import com.fatec.gini.dto.tipoSala.TipoSalaResponse;

public record SalaResponse(
        Long id,
        String codigo,
        Integer capacidade,
        TipoSalaResponse tipoSala
) {}
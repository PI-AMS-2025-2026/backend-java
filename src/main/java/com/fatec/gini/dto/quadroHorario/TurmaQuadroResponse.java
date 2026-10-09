package com.fatec.gini.dto.quadroHorario;

import java.util.List;

public record TurmaQuadroResponse(
        Long id,
        String codigo,
        Integer ano,
        Integer periodo,
        List<CelulaGradeResponse> celulas
) {
}

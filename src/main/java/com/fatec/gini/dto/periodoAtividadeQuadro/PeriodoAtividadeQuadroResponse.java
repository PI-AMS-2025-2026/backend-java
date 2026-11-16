package com.fatec.gini.dto.periodoAtividadeQuadro;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoPeridoAtividadeQuadro;

public record PeriodoAtividadeQuadroResponse(
        Long id,
        // Ano letivo ao qual pertence o período de vigência da grade.
        Integer ano,
        // Posição no ano letivo: 1/2 para semestral ou 1 para anual.
        Integer periodo,
        // Tipo da vigência da grade; determina a interpretação de periodo.
        TipoPeridoAtividadeQuadro tipoPeridoAtividadeQuadro,
        // Primeiro e último dia em que o quadro é válido.
        LocalDate dataInicio,
        LocalDate dataFim,
        // Estado administrativo do período de vigência.
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}

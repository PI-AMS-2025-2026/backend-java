package com.fatec.gini.dto.quadroHorario;

import com.fatec.gini.domain.models.Status;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record QuadroHorarioRequest(
    @NotNull(message = "Versão é obrigatória")
    Integer versao,
    
    @NotNull(message = "Status é obrigatório") // Enums usam NotNull, não NotBlank
    Status status,
    
    @NotNull(message = "Curso é obrigatório")
    LongDTO curso,
    
    @NotNull(message = "Período Atividade Quadro é obrigatório")
    LongDTO PeriodoAtividadeQuadro
) {}
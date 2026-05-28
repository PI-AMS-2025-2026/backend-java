package com.fatec.gini.dto.gradeHoraria;

import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record GradeHorariaRequest(
    @NotNull(message = "Versão é obrigatória")
    Integer versao,
    
    @NotNull(message = "Status é obrigatório") // Enums usam NotNull, não NotBlank
    Status status,
    
    @NotNull(message = "Curso é obrigatório")
    LongDTO curso,
    
    @NotNull(message = "Período Letivo é obrigatório")
    LongDTO periodoLetivo
) {}
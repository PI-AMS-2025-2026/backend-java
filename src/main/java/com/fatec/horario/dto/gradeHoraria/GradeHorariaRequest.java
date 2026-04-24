package com.fatec.horario.dto.gradeHoraria;

import com.fatec.horario.domain.entities.Status; // Importe o Enum
import com.fatec.horario.dto.id.LongDTO;
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
package com.fatec.horario.dto.gradeHoraria;

import com.fatec.horario.domain.entities.Status; // Importe o Enum
import jakarta.validation.constraints.NotNull;

public record GradeHorariaRequest(
    @NotNull(message = "Versão é obrigatória")
    Integer versao,
    
    @NotNull(message = "Status é obrigatório") // Enums usam NotNull, não NotBlank
    Status status,
    
    @NotNull(message = "Curso é obrigatório")
    Long idCurso,
    
    @NotNull(message = "Período Letivo é obrigatório")
    Long idPeriodoLetivo
) {}
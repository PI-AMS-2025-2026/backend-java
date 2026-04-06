package com.fatec.horario.dto.GradeHoraria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GradeHorariaRequest(
    @NotNull(message = "Versão é obrigatória")
    Integer versao,
    
    @NotBlank(message = "Status é obrigatório")
    String status,
    
    @NotNull(message = "Curso é obrigatório")
    Long cursoId,
    
    @NotNull(message = "Período Letivo é obrigatório")
    Long periodoLetivoId
) {}
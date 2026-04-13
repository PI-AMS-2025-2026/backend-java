package com.fatec.horario.dto.gradeHoraria;

import com.fatec.horario.domain.entities.Status; // Importe o Enum
import java.time.LocalDateTime;

public record GradeHorariaResponse(
    Long id,
    Integer versao,
    LocalDateTime dataCriacao,
    Status status, 
    Long idCurso,
    Long idPeriodoLetivo
) {}
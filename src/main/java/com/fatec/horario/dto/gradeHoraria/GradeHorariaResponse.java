package com.fatec.horario.dto.gradeHoraria;

import com.fatec.horario.domain.entities.Status; // Importe o Enum
import com.fatec.horario.dto.curso.CursoResponse;
import com.fatec.horario.dto.periodoLetivo.PeriodoLetivoResponse;
import java.time.LocalDateTime;

public record GradeHorariaResponse(
    Long id,
    Integer versao,
    LocalDateTime dataCriacao,
    Status status, 
    CursoResponse curso,
    PeriodoLetivoResponse periodoLetivo
) {}
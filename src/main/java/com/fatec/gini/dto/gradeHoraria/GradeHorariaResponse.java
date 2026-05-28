package com.fatec.gini.dto.gradeHoraria;

import java.time.LocalDateTime;

import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.dto.curso.CursoResponse;
import com.fatec.gini.dto.periodoLetivo.PeriodoLetivoResponse;

public record GradeHorariaResponse(
    Long id,
    Integer versao,
    LocalDateTime dataCriacao,
    Status status,
    CursoResponse curso,
    PeriodoLetivoResponse periodoLetivo,

    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
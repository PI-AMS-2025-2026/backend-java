package com.fatec.gini.dto.quadroHorario;

import java.time.LocalDateTime;

import com.fatec.gini.domain.models.Status;
import com.fatec.gini.dto.curso.CursoResponse;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroResponse;

public record QuadroHorarioResponse(
    Long id,
    Integer versao,
    LocalDateTime dataCriacao,
    Status status, 
    CursoResponse curso,
    PeriodoAtividadeQuadroResponse periodoAtividadeQuadro
) {}
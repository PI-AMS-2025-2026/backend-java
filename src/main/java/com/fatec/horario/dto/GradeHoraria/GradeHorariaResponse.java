package com.fatec.horario.dto.GradeHoraria;

import java.time.LocalDateTime;

public record GradeHorariaResponse(
    Long id,
    Integer versao,
    LocalDateTime dataCriacao,
    String status,
    Long cursoId,
    Long periodoLetivoId
) {}
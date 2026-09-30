package com.fatec.gini.dto.quadroHorario;

import com.fatec.gini.domain.models.Status;
import com.fatec.gini.dto.id.LongDTO;

import jakarta.validation.constraints.NotNull;

public record QuadroHorarioRequest(

    @NotNull(message = "Versão é obrigatória")
    Integer versao,

    @NotNull(message = "Status é obrigatório")
    Status status,

    @NotNull(message = "Curso é obrigatório")
    LongDTO curso,

    // ALTERAÇÃO: nome padronizado para camelCase no payload.
    @NotNull(message = "Período Atividade Quadro é obrigatório")
    LongDTO periodoAtividadeQuadro
) {}
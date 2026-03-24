package com.fatec.horario.dto.Horarios;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioRequest {

    @NotNull(message = "Hora de início é obrigatória")
    private LocalTime horaInicio;

    @NotNull(message = "Hora de fim é obrigatória")
    private LocalTime horaFim;

    @NotNull(message = "Duração é obrigatória")
    @Positive(message = "Duração deve ser positiva")
    private Integer duracao;

    @NotNull(message = "Tipo de usuário é obrigatório.")
    private Long accessLevelId;
}
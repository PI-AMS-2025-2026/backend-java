package com.fatec.horario.dto.Horarios;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioResponse {

    private Long id;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer duracao;
    private Long accessLevelId;
}
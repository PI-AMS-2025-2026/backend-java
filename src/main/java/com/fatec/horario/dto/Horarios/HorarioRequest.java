package com.fatec.horario.dto.Horarios;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

public class HorarioRequest {

    @NotNull(message = "Hora de início é obrigatória")
    private LocalTime horaInicio;

    @NotNull(message = "Hora de fim é obrigatória")
    private LocalTime horaFim;

    @NotNull(message = "Duração é obrigatória")
    @Positive(message = "Duração deve ser positiva")
    private Integer duracao;

    public HorarioRequest() {
    }

    public HorarioRequest(LocalTime horaInicio, LocalTime horaFim, Integer duracao) {
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracao = duracao;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

}
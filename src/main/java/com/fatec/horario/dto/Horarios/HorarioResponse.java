package com.fatec.horario.dto.Horarios;

import java.time.LocalTime;

public class HorarioResponse {

    private Long id;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer duracao;

    public HorarioResponse() {
    }

    public HorarioResponse(Long id, LocalTime horaInicio, LocalTime horaFim, Integer duracao) {
        this.id = id;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracao = duracao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
package com.fatec.horario.dto.RecursoSala;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RecursoSalaRequest {

    @NotNull
    private Long salaId;

    @NotNull
    private Long recursoId;

    @NotNull
    @Positive(message = "Quantidade deve ser maior que zero")
    private Integer quantidade;

    public RecursoSalaRequest() {}

    public Long getSalaId() { return salaId; }
    public void setSalaId(Long salaId) { this.salaId = salaId; }

    public Long getRecursoId() { return recursoId; }
    public void setRecursoId(Long recursoId) { this.recursoId = recursoId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
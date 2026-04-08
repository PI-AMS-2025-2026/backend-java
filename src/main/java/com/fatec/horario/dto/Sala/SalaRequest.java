package com.fatec.horario.dto.Sala;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class SalaRequest {

    @NotBlank(message = "Código é obrigatório")
    @Size(min = 2, max = 20, message = "Código deve ter entre 2 e 20 caracteres")
    private String codigo;

    @NotNull(message = "Capacidade é obrigatória")
    @Positive(message = "Capacidade deve ser maior que zero")
    private Integer capacidade;

    @NotNull(message = "Tipo de sala é obrigatório")
    @JsonProperty("tipo_sala")
    private Long tipoSalaId;

    // Construtores, getters e setters
    public SalaRequest() {}
    
    public SalaRequest(String codigo, Integer capacidade, Long tipoSalaId) {
        this.codigo = codigo;
        this.capacidade = capacidade;
        this.tipoSalaId = tipoSalaId;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public Long getTipoSalaId() { return tipoSalaId; }
    public void setTipoSalaId(Long tipoSalaId) { this.tipoSalaId = tipoSalaId; }
}
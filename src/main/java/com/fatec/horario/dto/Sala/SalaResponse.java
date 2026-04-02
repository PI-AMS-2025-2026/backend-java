package com.fatec.horario.dto.Sala;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SalaResponse {
    private Long id;
    private String codigo;
    private Integer capacidade;
    @JsonProperty("tipo_sala")
    private Long tipoSalaId;

    public SalaResponse() {}
    
    public SalaResponse(Long id, String codigo, Integer capacidade, Long tipoSalaId) {
        this.id = id;
        this.codigo = codigo;
        this.capacidade = capacidade;
        this.tipoSalaId = tipoSalaId;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public Long getTipoSalaId() { return tipoSalaId; }
    public void setTipoSalaId(Long tipoSalaId) { this.tipoSalaId = tipoSalaId; }
}
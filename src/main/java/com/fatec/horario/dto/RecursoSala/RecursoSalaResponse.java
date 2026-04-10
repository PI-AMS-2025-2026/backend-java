package com.fatec.horario.dto.RecursoSala;

public class RecursoSalaResponse {

    private Long id;
    private Long salaId;
    private Long recursoId;
    private Integer quantidade;

    public RecursoSalaResponse(Long id, Long salaId, Long recursoId, Integer quantidade) {
        this.id = id;
        this.salaId = salaId;
        this.recursoId = recursoId;
        this.quantidade = quantidade;
    }

    public Long getId() {
         return id; 
        }
    public Long getSalaId() { 
        return salaId; 
    }
    public Long getRecursoId() { 
        return recursoId; 
    }
    public Integer getQuantidade() { 
        return quantidade; 
    }
}
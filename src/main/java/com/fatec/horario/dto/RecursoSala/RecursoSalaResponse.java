package com.fatec.horario.dto.RecursoSala;

public record RecursoSalaResponse(

    Long id,
    Long salaId,
    Long recursoId,
    Integer quantidade
) {

        public RecursoSalaResponse(Long id, Long salaId, Long recursoId, Integer quantidade) {
        this.id = id;
        this.salaId = salaId;
        this.recursoId = recursoId;
        this.quantidade = quantidade;
    }
}
package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.dto.curso.CursoRequest;
import com.fatec.horario.dto.curso.CursoResponse;

public class CursoMapper {

    public static Curso toEntity(CursoRequest dto) {
        Curso curso = new Curso();
        curso.setNome(dto.nome());
        curso.setPeriodicidade(dto.periodicidade());
        curso.setStatus(dto.status());
        curso.setDuracao(dto.duracao());
        return curso;
    }

    public static CursoResponse toResponse(Curso curso) {
        return new CursoResponse(
                curso.getId(),
                curso.getNome(),
                curso.getPeriodicidade(),
                curso.getStatus(),
                curso.getDuracao());
    }
}
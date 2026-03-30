package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.dto.curso.CursoRequest;
import com.fatec.horario.dto.curso.CursoResponse;

public class CursoMapper {

    public static Curso toEntity(CursoRequest dto) {
        Curso curso = new Curso();
        curso.setNome(dto.getNome());
        curso.setPeriodicidade(dto.getPeriodicidade());
        curso.setStatus(dto.getStatus());
        curso.setDuracao(dto.getDuracao());
        return curso;
    }

    public static CursoResponse toResponse(Curso curso) {
        return new CursoResponse(
                curso.getIdCurso(),
                curso.getNome(),
                curso.getPeriodicidade(),
                curso.getStatus(),
                curso.getDuracao()
        );
    }
}
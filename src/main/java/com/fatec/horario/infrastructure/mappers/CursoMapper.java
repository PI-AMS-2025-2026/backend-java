package com.fatec.horario.infrastructure.mappers;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.dto.curso.CursoRequisicao;
import com.fatec.horario.dto.curso.CursoResposta;

public class CursoMapper {

    public static Curso paraEntidade(CursoRequisicao requisicao) {

        Curso curso = new Curso();

        curso.setNome(requisicao.nome());
        curso.setDescricao(requisicao.descricao());

        return curso;
    }

    public static CursoResposta paraResposta(Curso curso) {

        return new CursoResposta(
                curso.getId(),
                curso.getNome(),
                curso.getDescricao()
        );
    }
}
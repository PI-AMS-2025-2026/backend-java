package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.dto.curso.CursoRequisicao;
import com.fatec.horario.dto.curso.CursoResposta;
import com.fatec.horario.infrastructure.mappers.CursoMapper;
import com.fatec.horario.infrastructure.repositories.CursoRepositorio;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CursoServico {

    @Autowired
    private CursoRepositorio repositorio;

    @Transactional(readOnly = true)
    public List<CursoResposta> listarTodos() {

        return repositorio.findAll()
                .stream()
                .map(CursoMapper::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public CursoResposta buscarPorId(Long id) {

        Curso curso = repositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com id: " + id));

        return CursoMapper.paraResposta(curso);
    }

    @Transactional
    public CursoResposta criar(CursoRequisicao requisicao) {

        Curso curso = CursoMapper.paraEntidade(requisicao);

        curso = repositorio.save(curso);

        return CursoMapper.paraResposta(curso);
    }

    @Transactional
    public CursoResposta atualizar(Long id, CursoRequisicao requisicao) {

        Curso curso = repositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com id: " + id));

        curso.setNome(requisicao.nome());
        curso.setDescricao(requisicao.descricao());

        curso = repositorio.save(curso);

        return CursoMapper.paraResposta(curso);
    }

    @Transactional
    public void deletar(Long id) {

        if (!repositorio.existsById(id)) {
            throw new EntityNotFoundException("Curso não encontrado com id: " + id);
        }

        repositorio.deleteById(id);
    }
}
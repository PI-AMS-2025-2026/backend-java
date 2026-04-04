package com.fatec.horario.domain.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.dto.curso.CursoRequest;
import com.fatec.horario.dto.curso.CursoResponse;
import com.fatec.horario.infrastructure.mappers.CursoMapper;
import com.fatec.horario.infrastructure.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CursoService {

    private final CursoRepository repository;

    public CursoService(CursoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CursoResponse criar(CursoRequest dto) {
        Curso curso = CursoMapper.toEntity(dto);
        return CursoMapper.toResponse(repository.save(curso));
    }

    @Transactional(readOnly = true)
    public CursoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(CursoMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<CursoResponse> listar(
            String nome,
            String periodicidade,
            String status,
            Integer duracao,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);
        var pageCurso = repository.buscarPorFiltros(nome, periodicidade, status, duracao, pageRequest);
        return pageCurso.map(CursoMapper::toResponse);
    }

    @Transactional
    public CursoResponse atualizar(Long id, CursoRequest request) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + id));

        curso.setNome(request.nome());
        curso.setPeriodicidade(request.periodicidade());
        curso.setStatus(request.status());
        curso.setDuracao(request.duracao());

        return CursoMapper.toResponse(repository.save(curso));
    }

    /**
     * Essa entidade nunca pode ser deletada; em vez disso, ocorre a mudança de
     * status.
     *
     * @param id identificador do curso
     * 
     * @throws EntityNotFoundException caso curso não encontrado
     */
    @Transactional
    public void inativar(Long id) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + id));

        curso.setStatus("Inativo");

        repository.save(curso);
    }
}
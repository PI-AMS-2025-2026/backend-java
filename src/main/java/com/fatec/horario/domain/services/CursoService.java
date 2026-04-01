package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.dto.curso.CursoRequest;
import com.fatec.horario.dto.curso.CursoResponse;
import com.fatec.horario.infrastructure.repositories.CursoRepository;
import com.fatec.horario.infrastructure.mappers.CursoMapper;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<CursoResponse> listar() {
        return repository.findAll()
                .stream()
                .map(CursoMapper::toResponse)
                .toList();
    }

    @Transactional
    public CursoResponse atualizar(Long id, CursoRequest request) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        curso.setNome(request.getNome());
        curso.setPeriodicidade(request.getPeriodicidade());
        curso.setStatus(request.getStatus());
        curso.setDuracao(request.getDuracao());

        return CursoMapper.toResponse(repository.save(curso));
    }

    @Transactional
    public void deletar(Long id) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        repository.delete(curso);
    }
}
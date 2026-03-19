package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.dto.curso.CursoRequest;
import com.fatec.horario.dto.curso.CursoResponse;
import com.fatec.horario.infrastructure.repositories.CursoRepository;
import com.fatec.horario.infrastructure.mappers.CursoMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository repository;

    @Transactional
    public CursoResponse criar(CursoRequest dto) {
        Curso curso = CursoMapper.toEntity(dto);
        return CursoMapper.toResponse(repository.save(curso));
    }

    @Transactional(readOnly = true)
    public CursoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(CursoMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<CursoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable)
                .map(CursoMapper::toResponse);
    }

    @Transactional
    public CursoResponse atualizar(Long id, CursoRequest dto) {
        Curso curso = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        curso.setNome(dto.nome());
        curso.setPeriodicidade(dto.periodicidade());
        curso.setStatus(dto.status());
        curso.setDuracao(dto.duracao());

        return CursoMapper.toResponse(repository.save(curso));
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
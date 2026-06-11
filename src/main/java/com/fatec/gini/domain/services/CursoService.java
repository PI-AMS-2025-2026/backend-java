package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.dto.curso.CursoRequest;
import com.fatec.gini.dto.curso.CursoResponse;
import com.fatec.gini.infrastructure.mappers.CursoMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final  CursoRepository repository;

    @Transactional
    public CursoResponse criar(CursoRequest dto) {

        Curso curso = CursoMapper.toEntity(dto);
        LocalDateTime agora = LocalDateTime.now();
        curso.setCreatedAt(agora);
        curso.setUpdatedAt(agora);

        return CursoMapper.toResponse(repository.save(curso));
    }

    @Transactional(readOnly = true)
    public CursoResponse buscarPorId(Long id) {

        return repository.findById(id)
                .map(CursoMapper::toResponse)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Curso não encontrado com ID: " + id
                    )
                );
    }

    @Transactional(readOnly = true)
    public Page<CursoResponse> listar(
            String nome,
            String periodicidade,
            Status status,
            Integer duracao,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);

        var pageCurso = repository.buscarPorFiltros(
                nome,
                periodicidade,
                status,
                duracao,
                pageRequest
        );

        return pageCurso.map(CursoMapper::toResponse);
    }

    @Transactional
    public CursoResponse atualizar(Long id, CursoRequest request) {

        Curso curso = repository.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Curso não encontrado com ID: " + id
                    )
                );

        curso.setNome(request.nome());
        curso.setPeriodicidade(request.periodicidade());
        curso.setStatus(request.status());
        curso.setDuracao(request.duracao());
        curso.setUpdatedAt(LocalDateTime.now());

        return CursoMapper.toResponse(repository.save(curso));
    }

    @Transactional
    public void inativar(Long id) {

        Curso curso = repository.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Curso não encontrado com ID: " + id
                    )
                );

        curso.setStatus(Status.INATIVO);
        curso.setUpdatedAt(LocalDateTime.now());

        repository.save(curso);
    }
}
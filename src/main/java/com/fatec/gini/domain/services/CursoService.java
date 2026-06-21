package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.dto.curso.CursoRequest;
import com.fatec.gini.dto.curso.CursoResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.infrastructure.mappers.CursoMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository repository;

    @Transactional
    public CursoResponse criar(CursoRequest dto) {
        Curso entity = CursoMapper.toEntity(dto);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return CursoMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public CursoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(CursoMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public PageResponse<CursoResponse> listar(
            String nome,
            String periodicidade,
            Status status,
            Integer duracao,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);
        var page = repository.buscarPorFiltros(nome, periodicidade, status, duracao, pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(CursoMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public CursoResponse atualizar(Long id, CursoRequest request) {
        Curso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + id));

        entity.setNome(request.nome());
        entity.setPeriodicidade(request.periodicidade());
        entity.setStatus(request.status());
        entity.setDuracao(request.duracao());

        entity.setUpdatedAt(LocalDateTime.now());

        return CursoMapper.toResponse(repository.save(entity));
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
        Curso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + id));

        entity.setStatus(Status.INATIVO);

        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);
    }
}
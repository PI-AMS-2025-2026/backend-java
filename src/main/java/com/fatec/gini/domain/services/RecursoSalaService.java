package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Recurso;
import com.fatec.gini.domain.entities.RecursoSala;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.recursoSala.RecursoSalaRequest;
import com.fatec.gini.dto.recursoSala.RecursoSalaResponse;
import com.fatec.gini.infrastructure.mappers.RecursoSalaMapper;
import com.fatec.gini.infrastructure.repositories.RecursoRepository;
import com.fatec.gini.infrastructure.repositories.RecursoSalaRepository;
import com.fatec.gini.infrastructure.repositories.SalaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecursoSalaService {

    private final RecursoSalaRepository repository;

    private final SalaRepository salaRepository;

    private final RecursoRepository recursoRepository;

    @Transactional
    public RecursoSalaResponse criar(RecursoSalaRequest request) {

        // valida duplicidade
        if (repository.existsBySalaIdAndRecursoId(request.sala().id(), request.recurso().id())) {
            throw new IllegalArgumentException("Recurso já vinculado a essa sala");
        }

        // valida quantidade
        if (request.quantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        Sala sala = salaRepository.findById(request.sala().id())
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada"));

        Recurso recurso = recursoRepository.findById(request.recurso().id())
                .orElseThrow(() -> new EntityNotFoundException("Recurso não encontrado"));

        RecursoSala entity = new RecursoSala();
        entity.setQuantidade(request.quantidade());
        entity.setSala(sala);
        entity.setRecurso(recurso);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return RecursoSalaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public RecursoSalaResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(RecursoSalaMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Recurso da sala não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public PageResponse<RecursoSalaResponse> listar(
            Long salaId,
            Long recursoId,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);
        var page = repository.buscarPorFiltros(salaId, recursoId, pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(RecursoSalaMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public RecursoSalaResponse atualizar(Long id, RecursoSalaRequest request) {

        RecursoSala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurso da Sala não encontrado"));

        if (request.quantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        entity.setQuantidade(request.quantidade());
        entity.setUpdatedAt(LocalDateTime.now());
        return RecursoSalaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Recurso da Sala não encontrado");
        }
        repository.deleteById(id);
    }
}
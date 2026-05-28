package com.fatec.gini.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Recurso;
import com.fatec.gini.dto.recurso.RecursoRequest;
import com.fatec.gini.dto.recurso.RecursoResponse;
import com.fatec.gini.infrastructure.mappers.RecursoMapper;
import com.fatec.gini.infrastructure.repositories.RecursoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RecursoService {

    @Autowired
    private RecursoRepository repository;

    @Transactional
    public RecursoResponse criar(RecursoRequest request) {
        Recurso entity = RecursoMapper.toEntity(request);
        return RecursoMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public RecursoResponse buscarPorId(Long id) {
        Recurso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurso não encontrado com ID: " + id));
        return RecursoMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<RecursoResponse> listar(
            String nome,
            String tipo,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);
        var pageRecurso = repository.buscarPorFiltros(nome, tipo, pageRequest);
        return pageRecurso.map(RecursoMapper::toResponse);
    }

    @Transactional
    public RecursoResponse atualizar(Long id, RecursoRequest request) {
        Recurso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurso não encontrado com ID: " + id));

        entity.setNome(request.nome());
        entity.setTipo(request.tipo());

        return RecursoMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Recurso não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
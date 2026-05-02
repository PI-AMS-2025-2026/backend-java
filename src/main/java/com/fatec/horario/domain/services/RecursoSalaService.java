package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.Recurso;
import com.fatec.horario.domain.entities.RecursoSala;
import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.dto.recursoSala.RecursoSalaRequest;
import com.fatec.horario.dto.recursoSala.RecursoSalaResponse;
import com.fatec.horario.infrastructure.mappers.RecursoSalaMapper;
import com.fatec.horario.infrastructure.repositories.RecursoRepository;
import com.fatec.horario.infrastructure.repositories.RecursoSalaRepository;
import com.fatec.horario.infrastructure.repositories.SalaRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecursoSalaService {

    @Autowired
    private RecursoSalaRepository repository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private RecursoRepository recursoRepository;

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

        return RecursoSalaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public RecursoSalaResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(RecursoSalaMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Recurso da sala não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Page<RecursoSalaResponse> listar(
            Long salaId,
            Long recursoId,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);
        var result = repository.buscarPorFiltros(salaId, recursoId, pageRequest);

        return result.map(RecursoSalaMapper::toResponse);
    }

    @Transactional
    public RecursoSalaResponse atualizar(Long id, RecursoSalaRequest request) {

        RecursoSala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurso da Sala não encontrado"));

        if (request.quantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        entity.setQuantidade(request.quantidade());

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
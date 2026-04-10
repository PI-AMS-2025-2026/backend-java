package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.Recurso;
import com.fatec.horario.domain.entities.RecursoSala;
import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.dto.RecursoSala.RecursoSalaRequest;
import com.fatec.horario.dto.RecursoSala.RecursoSalaResponse;
import com.fatec.horario.infrastructure.mappers.RecursoSalaMapper;
import com.fatec.horario.infrastructure.repositories.RecursoRepository;
import com.fatec.horario.infrastructure.repositories.RecursoSalaRepository;
import com.fatec.horario.infrastructure.repositories.SalaRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecursoSalaService {

    private final RecursoSalaRepository repository;
    private final SalaRepository salaRepository;
    private final RecursoRepository recursoRepository;

    public RecursoSalaService(RecursoSalaRepository repository,
                              SalaRepository salaRepository,
                              RecursoRepository recursoRepository) {
        this.repository = repository;
        this.salaRepository = salaRepository;
        this.recursoRepository = recursoRepository;
    }

    @Transactional
    public RecursoSalaResponse criar(RecursoSalaRequest request) {

        if (repository.existsBySalaIdAndRecursoId(request.getSalaId(), request.getRecursoId())) {
            throw new IllegalArgumentException("Recurso já vinculado a essa sala");
        }

        if (request.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        Sala sala = salaRepository.findById(request.getSalaId())
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada"));

        Recurso recurso = recursoRepository.findById(request.getRecursoId())
                .orElseThrow(() -> new EntityNotFoundException("Recurso não encontrado"));

        RecursoSala entity = new RecursoSala(null, request.getQuantidade(), sala, recurso);

        return RecursoSalaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public RecursoSalaResponse buscarPorId(Long id) {
        RecursoSala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RecursoSala não encontrado"));

        return RecursoSalaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<RecursoSalaResponse> listar(Long salaId, Long recursoId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RecursoSala> result;

        if (salaId != null) {
            result = repository.findBySalaId(salaId, pageable);
        } else if (recursoId != null) {
            result = repository.findByRecursoId(recursoId, pageable);
        } else {
            result = repository.findAll(pageable);
        }

        return result.map(RecursoSalaMapper::toResponse);
    }

    @Transactional
    public RecursoSalaResponse atualizar(Long id, RecursoSalaRequest request) {

        RecursoSala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RecursoSala não encontrado"));

        if (request.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        entity.setQuantidade(request.getQuantidade());

        return RecursoSalaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("RecursoSala não encontrado");
        }
        repository.deleteById(id);
    }
}
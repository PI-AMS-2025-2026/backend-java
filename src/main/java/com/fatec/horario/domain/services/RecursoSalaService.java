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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecursoSalaService {

    @Autowired
    private RecursoSalaRepository repository;
    private SalaRepository salaRepository;
    private RecursoRepository recursoRepository;

    @Transactional
    public RecursoSalaResponse criar(RecursoSalaRequest request) {
        RecursoSala entity = RecursoSalaMapper.toEntity(request);
    
        if (repository.existsBySalaIdAndRecursoId(request.salaId(), request.recursoId())) {
            throw new IllegalArgumentException("Recurso já vinculado a essa sala");
        }

        if (request.quantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        Sala sala = salaRepository.findById(request.salaId())
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada"));

        Recurso recurso = recursoRepository.findById(request.recursoId())
                .orElseThrow(() -> new EntityNotFoundException("Recurso não encontrado"));

        RecursoSala entity = new RecursoSala(null, request.quantidade(), sala, recurso);

        return RecursoSalaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public RecursoSalaResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(RecursoSalaMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("RecursoSala não foi encontrado." + id));
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

        if (request.quantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        entity.setQuantidade(request.quantidade());

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
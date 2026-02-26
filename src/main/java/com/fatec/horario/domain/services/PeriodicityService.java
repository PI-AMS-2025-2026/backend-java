package com.fatec.horario.domain.services;

import com.fatec.horario.infrastructure.mappers.PeriodicityMapper;
import com.fatec.horario.infrastructure.repositories.PeriodicityRepository;
import com.fatec.horario.domain.entities.Periodicity;
import com.fatec.horario.dto.Periodicity.PeriodicityRequest;
import com.fatec.horario.dto.Periodicity.PeriodicityResponse;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodicityService {

    @Autowired
    private PeriodicityRepository repository;

    public List<PeriodicityResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(PeriodicityMapper::toResponse)
                .toList();
    }

    public PeriodicityResponse getById(Long id) {
        Periodicity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Periodicity not found with id: " + id));
        return PeriodicityMapper.toResponse(entity);
    }

    public PeriodicityResponse create(PeriodicityRequest request) {
        Periodicity periodicity = PeriodicityMapper.toEntity(request);
        periodicity = repository.save(periodicity);
        return PeriodicityMapper.toResponse(periodicity);
    }

    public PeriodicityResponse update(Long id, PeriodicityRequest request) {
        Periodicity periodicity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Periodicity not found with id: " + id));
        periodicity.setDescription(request.description());
        periodicity = repository.save(periodicity);
        return PeriodicityMapper.toResponse(periodicity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Periodicity not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

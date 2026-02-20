package com.fatec.horario.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.model.TechAxis;
import com.fatec.horario.dto.TechAxisRequest;
import com.fatec.horario.dto.TechAxisResponse;
import com.fatec.horario.infrastructure.mapper.TechAxisMapper;
import com.fatec.horario.infrastructure.repository.TechAxisRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TechAxisService {

    @Autowired
    private TechAxisRepository repository;

    public TechAxisResponse getById(Long id) {
        TechAxis techAxis = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TechAxis not found with id: " + id));

        return TechAxisMapper.toResponse(techAxis);
    }

    public List<TechAxisResponse> getAll() {
        return repository.findAll().stream()
                .map(TechAxisMapper::toResponse)
                .toList();
    }

    public TechAxisResponse create(TechAxisRequest request) {
        TechAxis techAxis = TechAxisMapper.toEntity(request);
        techAxis = repository.save(techAxis);
        return TechAxisMapper.toResponse(techAxis);
    }

    public TechAxisResponse update(Long id, TechAxisRequest request) {
        TechAxis techAxis = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TechAxis not found with id: " + id));

        techAxis.setName(request.name());
        techAxis = repository.save(techAxis);

        return TechAxisMapper.toResponse(techAxis);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("TechAxis not found with id: " + id);
        }

        repository.deleteById(id);
    }
}

package com.fatec.horario.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.model.Modality;
import com.fatec.horario.dto.ModalityRequest;
import com.fatec.horario.dto.ModalityResponse;
import com.fatec.horario.infrastructure.mapper.ModalityMapper;
import com.fatec.horario.infrastructure.repository.ModalityRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ModalityService {

    @Autowired
    private ModalityRepository repository;

    public List<ModalityResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(ModalityMapper::toResponse)
                .toList();
    }

    public ModalityResponse getById(long id) {
        return repository.findById(id)
                .map(ModalityMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Modality not registered"));
    }

    public void delete(long id) {
        if (repository.existsById(id))
            repository.deleteById(id);
        else
            throw new EntityNotFoundException("Modality not exist");

    }

    public ModalityResponse create(ModalityRequest request) {
        Modality modality = ModalityMapper.toEntity(request);
        modality = repository.save(modality);
        return ModalityMapper.toResponse(modality);
    }

    public ModalityResponse update(ModalityRequest request, long id) {
        Modality modality = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Modality not found"));

        modality.setName(request.name());

        modality = repository.save(modality);
        return ModalityMapper.toResponse(modality);
    }

}

package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.horario.domain.entities.AccessLevel;
import com.fatec.horario.dto.AcessLevel.AccessLevelRequest;
import com.fatec.horario.dto.AcessLevel.AccessLevelResponse;
import com.fatec.horario.infrastructure.mappers.AccessLevelMapper;
import com.fatec.horario.infrastructure.repositories.AccessLevelRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AccessLevelService {

    @Autowired
    private AccessLevelRepository repository;

    public List<AccessLevelResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(AccessLevelMapper::toResponse)
                .toList();
    }

    public AccessLevelResponse getById(Long id) {
        AccessLevel accessLevel = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AccessLevel not found with id " + id));

        return AccessLevelMapper.toResponse(accessLevel);
    }

    public AccessLevelResponse create(AccessLevelRequest request) {
        AccessLevel accessLevel = AccessLevelMapper.toEntity(request);
        accessLevel = repository.save(accessLevel);

        return AccessLevelMapper.toResponse(accessLevel);
    }

    public AccessLevelResponse update(Long id, AccessLevelRequest request) {
        AccessLevel accessLevel = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AccessLevel not found with id " + id));

        accessLevel.setLevel(request.level());
        accessLevel.setDescription(request.description());

        accessLevel = repository.save(accessLevel);

        return AccessLevelMapper.toResponse(accessLevel);
    }

    public void delete(Long id) {
        AccessLevel accessLevel = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AccessLevel not found with id " + id));

        repository.delete(accessLevel);
    }

}

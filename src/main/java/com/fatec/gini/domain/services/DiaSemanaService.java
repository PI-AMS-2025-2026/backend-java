package com.fatec.gini.domain.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.dto.diaSemana.DiaSemanaRequest;
import com.fatec.gini.dto.diaSemana.DiaSemanaResponse;
import com.fatec.gini.infrastructure.mappers.DiaSemanaMapper;
import com.fatec.gini.infrastructure.repositories.DiaSemanaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaSemanaService {

    private final  DiaSemanaRepository repository;

    @Transactional
    public DiaSemanaResponse criar(DiaSemanaRequest request) {
        DiaSemana entity = DiaSemanaMapper.toEntity(request);
        entity = repository.save(entity);
        return DiaSemanaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public DiaSemanaResponse buscarPorId(Long id) {
        DiaSemana entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dia da semana não encontrado com ID: " + id));
        return DiaSemanaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<DiaSemanaResponse> listar() {
        return repository.findAll().stream()
                .map(DiaSemanaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiaSemanaResponse atualizar(Long id, DiaSemanaRequest request) {
        DiaSemana entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dia da semana não encontrado com ID: " + id));
        entity.setNome(request.nome());
        entity = repository.save(entity);
        return DiaSemanaMapper.toResponse(entity);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Dia da semana não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
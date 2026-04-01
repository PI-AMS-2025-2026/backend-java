package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.dto.diaSemana.DiaSemanaRequest;
import com.fatec.horario.dto.diaSemana.DiaSemanaResponse;
import com.fatec.horario.infrastructure.mappers.DiaSemanaMapper;
import com.fatec.horario.infrastructure.repositories.DiaSemanaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiaSemanaService {

    private final DiaSemanaRepository repository;

    public DiaSemanaService(DiaSemanaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DiaSemanaResponse criar(DiaSemanaRequest request) {
        DiaSemana entity = DiaSemanaMapper.toEntity(request);
        entity = repository.save(entity);
        return DiaSemanaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public DiaSemanaResponse buscarPorId(Long id) {
        DiaSemana entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dia da semana não encontrado: " + id));
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
                .orElseThrow(() -> new EntityNotFoundException("Dia da semana não encontrado: " + id));
        entity.setNome(request.nome());
        entity = repository.save(entity);
        return DiaSemanaMapper.toResponse(entity);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Dia da semana não encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
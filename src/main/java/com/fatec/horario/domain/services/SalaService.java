package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.domain.entities.TipoSala;
import com.fatec.horario.dto.Sala.SalaRequest;
import com.fatec.horario.dto.Sala.SalaResponse;
import com.fatec.horario.infrastructure.mappers.SalaMapper;
import com.fatec.horario.infrastructure.repositories.SalaRepository;
import com.fatec.horario.infrastructure.repositories.TipoSalaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalaService {

    private final SalaRepository repository;
    private final TipoSalaRepository tipoSalaRepository;

    public SalaService(SalaRepository repository, TipoSalaRepository tipoSalaRepository) {
        this.repository = repository;
        this.tipoSalaRepository = tipoSalaRepository;
    }

    @Transactional
    public SalaResponse criar(SalaRequest request) {
        TipoSala tipoSala = tipoSalaRepository.findById(request.getTipoSalaId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de sala não encontrado"));

        // Validação de código único
        if (repository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma sala com este código");
        }

        Sala entity = SalaMapper.toEntity(request, tipoSala);
        entity = repository.save(entity);
        return SalaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public SalaResponse buscarPorId(Long id) {
        Sala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada com ID: " + id));
        return SalaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<SalaResponse> listar(Long tipoSalaId, Integer capacidade, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Sala> result;

        if (tipoSalaId != null && capacidade != null) {
            result = repository.findByTipoSalaIdAndCapacidade(tipoSalaId, capacidade, pageable);
        } else if (tipoSalaId != null) {
            result = repository.findByTipoSalaId(tipoSalaId, pageable);
        } else if (capacidade != null) {
            result = repository.findByCapacidade(capacidade, pageable);
        } else {
            result = repository.findAll(pageable);
        }

        return result.map(SalaMapper::toResponse);
    }

    @Transactional
    public SalaResponse atualizar(Long id, SalaRequest request) {
        Sala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada"));

        TipoSala tipoSala = tipoSalaRepository.findById(request.getTipoSalaId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de sala não encontrado"));

        entity.setCodigo(request.getCodigo());
        entity.setCapacidade(request.getCapacidade());
        entity.setTipoSala(tipoSala);

        entity = repository.save(entity);
        return SalaMapper.toResponse(entity);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Sala não encontrada");
        }
        repository.deleteById(id);
    }
}
package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.TipoSala;
import com.fatec.gini.domain.services.usecase.read.ValidarSalaSemVinculosUseCase;
import com.fatec.gini.dto.sala.SalaRequest;
import com.fatec.gini.dto.sala.SalaResponse;
import com.fatec.gini.infrastructure.mappers.SalaMapper;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.infrastructure.repositories.TipoSalaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SalaService {

    @Autowired
    private SalaRepository repository;

    @Autowired
    private TipoSalaRepository tipoSalaRepository;

    @Autowired
    private ValidarSalaSemVinculosUseCase validarSalaSemVinculos;

    @Transactional
    public SalaResponse criar(SalaRequest request) {
        Sala entity = SalaMapper.toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreated_at(now);
        entity.setUpdated_at(now);

        TipoSala tipo = tipoSalaRepository.findById(request.tipoSala().id())
            .orElseThrow(() -> new EntityNotFoundException("Tipo de sala não encontrado com ID: " + request.tipoSala().id()));
        entity.setTipoSala(tipo);

        return SalaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public SalaResponse buscarPorId(long id) {
        Sala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada com ID: " + id));
        return SalaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<SalaResponse> listar(
            Long idTipoSala,
            Integer capacidade,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);
        var pageSala = repository.buscarPorFiltros(idTipoSala, capacidade, pageRequest);
        return pageSala.map(SalaMapper::toResponse);
    }

    @Transactional
    public SalaResponse atualizar(long id, SalaRequest request) {
        Sala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada com ID: " + id));

        TipoSala tipo = tipoSalaRepository.findById(request.tipoSala().id())
            .orElseThrow(() -> new EntityNotFoundException("Tipo de sala não encontrado com ID: " + request.tipoSala().id()));

        entity.setCodigo(request.codigo());
        entity.setCapacidade(request.capacidade());
        entity.setTipoSala(tipo);
        entity.setUpdated_at(LocalDateTime.now());

        return SalaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Sala não encontrada com ID: " + id);
        }
        validarSalaSemVinculos.validar(id);
        repository.deleteById(id);
    }
}
package com.fatec.horario.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.domain.entities.TipoSala;
import com.fatec.horario.dto.sala.SalaRequest;
import com.fatec.horario.dto.sala.SalaResponse;
import com.fatec.horario.infrastructure.mappers.SalaMapper;
import com.fatec.horario.infrastructure.repositories.SalaRepository;
import com.fatec.horario.infrastructure.repositories.TipoSalaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SalaService {

    @Autowired
    private SalaRepository repository;

    @Autowired
    private TipoSalaRepository tipoSalaRepository;

    @Transactional
    public SalaResponse criar(SalaRequest request) {
        Sala entity = SalaMapper.toEntity(request);
                TipoSala tipo = tipoSalaRepository.findById(request.idTipoSala())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de sala não encontrado com ID: " + request.idTipoSala()));
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

        TipoSala tipo = tipoSalaRepository.findById(request.idTipoSala())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de sala não encontrado com ID: " + request.idTipoSala()));

        entity.setCodigo(request.codigo());
        entity.setCapacidade(request.capacidade());
        entity.setTipoSala(tipo);

        return SalaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Sala não encontrada com ID: " + id);
        }
        repository.deleteById(id);
    }
}
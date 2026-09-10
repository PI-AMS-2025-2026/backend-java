package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.TipoSala;
import com.fatec.gini.domain.services.usecase.read.ValidarSalaSemVinculosUseCase;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.sala.SalaRequest;
import com.fatec.gini.dto.sala.SalaResponse;
import com.fatec.gini.infrastructure.mappers.SalaMapper;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.infrastructure.repositories.TipoSalaRepository;
import com.fatec.gini.web.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository repository;

    private final TipoSalaRepository tipoSalaRepository;

    private final ValidarSalaSemVinculosUseCase validarSalaSemVinculos;

    @Transactional
    public SalaResponse criar(SalaRequest request) {

        if (repository.existsByCodigoIgnoreCase(request.codigo())) {
            throw new BusinessException(
                    "Já existe uma sala cadastrada com o código: "
                            + request.codigo());
        }

        Sala entity = SalaMapper.toEntity(request);

        TipoSala tipo = tipoSalaRepository.findById(request.tipoSala().id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de sala não encontrado com ID: "
                                + request.tipoSala().id()));

        entity.setTipoSala(tipo);

        LocalDateTime agora = LocalDateTime.now();

        entity.setCreatedAt(agora);
        entity.setUpdatedAt(agora);

        return SalaMapper.toResponse(
                repository.save(entity));
    }

    @Transactional(readOnly = true)
    public SalaResponse buscarPorId(long id) {

        Sala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Sala não encontrada com ID: " + id));

        return SalaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public PageResponse<SalaResponse> listar(
            Long idTipoSala,
            Integer capacidade,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(
                pageNum,
                size);

        var page = repository.buscarPorFiltros(
                idTipoSala,
                capacidade,
                pageRequest);

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(SalaMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public SalaResponse atualizar(
            long id,
            SalaRequest request) {

        Sala entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Sala não encontrada com ID: " + id));

        if (repository.existsByCodigoIgnoreCaseAndIdNot(
                request.codigo(),
                id)) {

            throw new BusinessException(
                    "Já existe outra sala cadastrada com o código: "
                            + request.codigo());
        }

        TipoSala tipo = tipoSalaRepository.findById(
                request.tipoSala().id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de sala não encontrado com ID: "
                                + request.tipoSala().id()));

        entity.setCodigo(request.codigo());
        entity.setCapacidade(request.capacidade());
        entity.setTipoSala(tipo);
        entity.setUpdatedAt(LocalDateTime.now());

        return SalaMapper.toResponse(
                repository.save(entity));
    }

    @Transactional
    public void deletar(long id) {

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Sala não encontrada com ID: " + id);
        }

        validarSalaSemVinculos.validar(id);

        repository.deleteById(id);
    }
}
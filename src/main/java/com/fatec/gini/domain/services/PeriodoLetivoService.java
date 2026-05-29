package com.fatec.gini.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.PeriodoLetivo;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.dto.periodoLetivo.PeriodoLetivoRequest;
import com.fatec.gini.dto.periodoLetivo.PeriodoLetivoResponse;
import com.fatec.gini.infrastructure.mappers.PeriodoLetivoMapper;
import com.fatec.gini.infrastructure.repositories.PeriodoLetivoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PeriodoLetivoService {

    @Autowired
    private PeriodoLetivoRepository repository;

    @Transactional
    public PeriodoLetivoResponse criar(PeriodoLetivoRequest request) {

        PeriodoLetivo entity = PeriodoLetivoMapper.toEntity(request);

        PeriodoLetivo periodoCriado = repository.save(entity);

        return PeriodoLetivoMapper.toResponse(periodoCriado);
    }

    @Transactional(readOnly = true)
    public PeriodoLetivoResponse buscarPorId(long id) {

        PeriodoLetivo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Período letivo não encontrado com ID: " + id));

        return PeriodoLetivoMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<PeriodoLetivoResponse> listar(
            Integer ano,
            Integer periodo,
            Status status,
            java.time.LocalDate dataInicio,
            java.time.LocalDate dataFim,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);

        var pagePeriodo = repository.buscarPorFiltros(
                ano,
                periodo,
                status,
                dataInicio,
                dataFim,
                pageRequest);

        return pagePeriodo.map(PeriodoLetivoMapper::toResponse);
    }

    @Transactional
    public PeriodoLetivoResponse atualizar(long id, PeriodoLetivoRequest request) {

        PeriodoLetivo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Período letivo não encontrado com ID: " + id));

        entity.setAno(request.ano());
        entity.setPeriodo(request.periodo());
        entity.setDataInicio(request.dataInicio());
        entity.setDataFim(request.dataFim());
        entity.setStatus(request.status());

        PeriodoLetivo periodoAtualizado = repository.save(entity);

        return PeriodoLetivoMapper.toResponse(periodoAtualizado);
    }

    /**
     * Inativa um período letivo; em vez disso, ocorre a mudança de status.
     *
     * @param id identificador do período letivo
     *
     * @throws EntityNotFoundException caso período não encontrado
     */
    @Transactional
    public void inativar(long id) {

        var periodo = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Período letivo não encontrado com ID: " + id));

        periodo.setStatus(Status.INATIVO);

        repository.save(periodo);
    }
}
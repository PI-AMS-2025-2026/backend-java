package com.fatec.horario.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.PeriodoLetivo;
import com.fatec.horario.dto.periodoLetivo.PeriodoLetivoRequest;
import com.fatec.horario.dto.periodoLetivo.PeriodoLetivoResponse;
import com.fatec.horario.infrastructure.mappers.PeriodoLetivoMapper;
import com.fatec.horario.infrastructure.repositories.PeriodoLetivoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PeriodoLetivoService {

    @Autowired
    private PeriodoLetivoRepository repository;

    @Transactional
    public PeriodoLetivoResponse criar(PeriodoLetivoRequest request) {
        PeriodoLetivo entity = PeriodoLetivoMapper.toEntity(request);
        return PeriodoLetivoMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public PeriodoLetivoResponse buscarPorId(long id) {
        PeriodoLetivo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Periodo letivo não encontrado com ID: " + id));
        return PeriodoLetivoMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<PeriodoLetivoResponse> listar(
            Integer ano,
            Integer periodo,
            String status,
            java.time.LocalDate dataInicio,
            java.time.LocalDate dataFim,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);
        var pagePeriodo = repository.buscarPorFiltros(ano, periodo, status, dataInicio, dataFim, pageRequest);
        return pagePeriodo.map(PeriodoLetivoMapper::toResponse);
    }

    @Transactional
    public PeriodoLetivoResponse atualizar(long id, PeriodoLetivoRequest request) {
        PeriodoLetivo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Periodo letivo não encontrado com ID: " + id));

        entity.setAno(request.ano());
        entity.setPeriodo(request.periodo());
        entity.setDataInicio(request.dataInicio());
        entity.setDataFim(request.dataFim());
        entity.setStatus(request.status());

        return PeriodoLetivoMapper.toResponse(repository.save(entity));
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
                .orElseThrow(() -> new EntityNotFoundException("Periodo letivo não encontrado com ID: " + id));

        periodo.setStatus("Inativo");
        repository.save(periodo);
    }

}

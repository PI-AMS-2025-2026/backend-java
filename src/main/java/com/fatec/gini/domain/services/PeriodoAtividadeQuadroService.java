package com.fatec.gini.domain.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarDataPeriodoAtividadeQuadroUseCase;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroRequest;
import com.fatec.gini.dto.periodoAtividadeQuadro.PeriodoAtividadeQuadroResponse;
import com.fatec.gini.infrastructure.mappers.PeriodoAtividadeQuadroMapper;
import com.fatec.gini.infrastructure.repositories.PeriodoAtividadeQuadroRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PeriodoAtividadeQuadroService {

    private final PeriodoAtividadeQuadroRepository repository;
    private final ValidarDataPeriodoAtividadeQuadroUseCase atividadeQuadroUseCase;

    @Transactional
    public PeriodoAtividadeQuadroResponse criar(PeriodoAtividadeQuadroRequest request) {
        atividadeQuadroUseCase.executar(request.dataInicio(), request.dataFim());
        atividadeQuadroUseCase.executarAno(request.ano(), request.dataInicio());

        PeriodoAtividadeQuadro entity = PeriodoAtividadeQuadroMapper.toEntity(request);
        return PeriodoAtividadeQuadroMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public PeriodoAtividadeQuadroResponse buscarPorId(long id) {
        PeriodoAtividadeQuadro entity = repository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Período Atividade Quadro não encontrado com ID: " + id));
        return PeriodoAtividadeQuadroMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public PageResponse<PeriodoAtividadeQuadroResponse> listar(
            Integer ano,
            Integer periodo,
            Status status,
            java.time.LocalDate dataInicio,
            java.time.LocalDate dataFim,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);
        var page = repository.buscarPorFiltros(ano, periodo, status, dataInicio, dataFim, pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(PeriodoAtividadeQuadroMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public PeriodoAtividadeQuadroResponse atualizar(long id, PeriodoAtividadeQuadroRequest request) {
        atividadeQuadroUseCase.executar(request.dataInicio(), request.dataFim());
        atividadeQuadroUseCase.executarAno(request.ano(), request.dataInicio());


        PeriodoAtividadeQuadro entity = repository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Período Atividade Quadro não encontrado com ID:  " + id));

        entity.setAno(request.ano());
        entity.setPeriodo(request.periodo());
        entity.setDataInicio(request.dataInicio());
        entity.setDataFim(request.dataFim());
        entity.setStatus(request.status());
        return PeriodoAtividadeQuadroMapper.toResponse(repository.save(entity));
    }

    /**
     * Inativa um período atividade quadro; em vez disso, ocorre a mudança de
     * status.
     *
     * @param id identificador do período atividade quadro
     * 
     * @throws EntityNotFoundException caso período não encontrado
     */
    @Transactional
    public void inativar(long id) {

        var entity = repository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Período Atividade Quadro não encontrado com ID:  " + id));

        entity.setStatus(Status.INATIVO);
        repository.save(entity);
    }

}

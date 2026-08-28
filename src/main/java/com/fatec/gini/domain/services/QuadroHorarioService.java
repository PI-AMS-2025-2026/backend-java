package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarCursoAtivoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarPeriodoAtividadeQuadroAtivoUseCase;
import com.fatec.gini.domain.services.usecase.write.CopiarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.write.ValidarQuadroHorarioValidoUseCase;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioRequest;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioResponse;
import com.fatec.gini.infrastructure.mappers.QuadroHorarioMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.PeriodoAtividadeQuadroRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuadroHorarioService {

    private final QuadroHorarioRepository repository;

    private final CursoRepository cursoRepository;

    private final PeriodoAtividadeQuadroRepository periodoRepository;

    private final ValidarQuadroHorarioValidoUseCase validarQuadroHorarioValidoUseCase;

    private final ValidarPeriodoAtividadeQuadroAtivoUseCase validarPeriodoAtividadeQuadroAtivoUseCase;

    private final ValidarCursoAtivoUseCase validarCursoAtivoUseCase;

    private final CopiarQuadroHorarioUseCase copiarQuadroHorarioUseCase;

    @Transactional
    public QuadroHorarioResponse copiar(
            Long id,
            QuadroHorarioRequest request) {

        QuadroHorario dadosNovaGrade =
                QuadroHorarioMapper.toEntity(request);

        PeriodoAtividadeQuadro periodo =
                periodoRepository.findById(
                        request.PeriodoAtividadeQuadro().id())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Período Atividade Quadro não encontrado com ID: "
                                        + request.PeriodoAtividadeQuadro().id()));

        dadosNovaGrade.setPeriodoAtividadeQuadro(periodo);

        QuadroHorario copia =
                copiarQuadroHorarioUseCase.executar(
                        id,
                        dadosNovaGrade);

        return QuadroHorarioMapper.toResponse(copia);
    }

    @Transactional
    public QuadroHorarioResponse criar(
            QuadroHorarioRequest request) {

        QuadroHorario entity =
                QuadroHorarioMapper.toEntity(request);

        Curso curso =
                cursoRepository.findById(
                        request.curso().id())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Curso não encontrado com ID: "
                                        + request.curso().id()));

        validarCursoAtivoUseCase.validar(curso);

        PeriodoAtividadeQuadro periodo =
                periodoRepository.findById(
                        request.PeriodoAtividadeQuadro().id())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Período Atividade Quadro não encontrado com ID: "
                                        + request.PeriodoAtividadeQuadro().id()));

        entity.setCurso(curso);
        entity.setPeriodoAtividadeQuadro(periodo);
        entity.setDataCriacao(LocalDateTime.now());
        entity.setStatus(request.status());

        validarQuadroHorarioValidoUseCase
                .validarQuadroAtivoDuplicado(entity);

        validarPeriodoAtividadeQuadroAtivoUseCase
                .executar(entity);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return QuadroHorarioMapper.toResponse(
                repository.save(entity));
    }

    @Transactional(readOnly = true)
    public QuadroHorarioResponse buscarPorId(
            Long id) {

        QuadroHorario entity =
                repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Quadro horário não encontrada com ID: "
                                        + id));

        return QuadroHorarioMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public PageResponse<QuadroHorarioResponse> listar(
            Long idCurso,
            Long idPeriodoAtividadeQuadro,
            Status status,
            int pageNum,
            int size) {

        var pageRequest =
                PageRequest.of(pageNum, size);

        var page =
                repository.buscarComFiltros(
                        idCurso,
                        idPeriodoAtividadeQuadro,
                        status,
                        pageRequest);

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(QuadroHorarioMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public QuadroHorarioResponse atualizar(
            Long id,
            QuadroHorarioRequest request) {

        QuadroHorario entity =
                repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Quadro horário não encontrada com ID: "
                                        + id));

        Curso curso =
                cursoRepository.findById(
                        request.curso().id())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Curso não encontrado com ID: "
                                        + request.curso().id()));

        validarCursoAtivoUseCase.validar(curso);

        PeriodoAtividadeQuadro periodo =
                periodoRepository.findById(
                        request.PeriodoAtividadeQuadro().id())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Período Atividade Quadro não encontrado com ID: "
                                        + request.PeriodoAtividadeQuadro().id()));

        entity.setVersao(request.versao());
        entity.setStatus(request.status());
        entity.setCurso(curso);
        entity.setPeriodoAtividadeQuadro(periodo);

        validarQuadroHorarioValidoUseCase
                .validarQuadroAtivoDuplicado(entity);

        validarPeriodoAtividadeQuadroAtivoUseCase
                .executar(entity);

        entity.setUpdatedAt(LocalDateTime.now());

        return QuadroHorarioMapper.toResponse(
                repository.save(entity));
    }

    @Transactional
    public void inativar(Long id) {

        QuadroHorario entity =
                repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Quadro horário não encontrada com ID: "
                                        + id));

        entity.setStatus(Status.INATIVO);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);
    }
}
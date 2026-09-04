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

    private final CopiarQuadroHorarioUseCase copiarQuadroHorarioUseCase;

    private final ValidarCursoAtivoUseCase validarCursoAtivoUseCase;

    @Transactional
    public QuadroHorarioResponse copiar(
            Long id,
            QuadroHorarioRequest request) {

        /*
         * Verifica se o quadro de origem existe.
         */
        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Quadro horário não encontrada com ID: " + id));

        /*
         * Converte os dados da requisição para a entidade.
         *
         * O CopiarQuadroHorarioUseCase utiliza principalmente
         * a versão e o período da nova grade.
         */
        QuadroHorario dadosNovaGrade =
                QuadroHorarioMapper.toEntity(request);

        dadosNovaGrade.setPeriodoAtividadeQuadro(
                periodoRepository.findById(
                        request.periodoAtividadeQuadro().id())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Período Atividade Quadro não encontrado com ID: "
                                        + request.periodoAtividadeQuadro().id())));

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

        /*
         * Busca o curso informado.
         */
        Curso curso =
                cursoRepository.findById(
                        request.curso().id())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Curso não encontrado com ID: "
                                        + request.curso().id()));

        /*
         * O curso precisa estar ativo para criação
         * de um quadro horário.
         */
        validarCursoAtivoUseCase.validar(curso);

        /*
         * Busca o período informado.
         */
        PeriodoAtividadeQuadro periodo =
                periodoRepository.findById(
                        request.periodoAtividadeQuadro().id())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Período Atividade Quadro não encontrado com ID:  "
                                        + request.periodoAtividadeQuadro().id()));

        entity.setCurso(curso);
        entity.setPeriodoAtividadeQuadro(periodo);

        entity.setDataCriacao(
                LocalDateTime.now());

        /*
         * Todo novo quadro horário deve iniciar como ATIVO,
         * independentemente do status enviado na requisição.
         */
        entity.setStatus(Status.ATIVO);

        /*
         * Verifica se já existe outro quadro ativo
         * para o mesmo curso e período.
         */
        validarQuadroHorarioValidoUseCase
                .validarQuadroAtivoDuplicado(entity);

        /*
         * Valida se o período está ativo.
         */
        validarPeriodoAtividadeQuadroAtivoUseCase
                .executar(entity);

        LocalDateTime agora =
                LocalDateTime.now();

        entity.setCreatedAt(agora);
        entity.setUpdatedAt(agora);

        return QuadroHorarioMapper.toResponse(
                repository.save(entity));
    }

    @Transactional(readOnly = true)
    public QuadroHorarioResponse buscarPorId(
            Long id) {

        QuadroHorario entity =
                repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Quadro horário não encontrada com ID: " + id));

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
                PageRequest.of(
                        pageNum,
                        size);

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
                                "Quadro horário não encontrada com ID: " + id));

        /*
         * Busca o curso informado na atualização.
         */
        Curso curso =
                cursoRepository.findById(
                        request.curso().id())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Curso não encontrado com ID: "
                                        + request.curso().id()));

        /*
         * O curso precisa estar ativo para alteração
         * do quadro horário.
         */
        validarCursoAtivoUseCase.validar(curso);

        /*
         * Busca o período informado.
         */
        PeriodoAtividadeQuadro periodo =
                periodoRepository.findById(
                        request.periodoAtividadeQuadro().id())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Período Atividade Quadro não encontrado com ID:  "
                                        + request.periodoAtividadeQuadro().id()));

        entity.setVersao(
                request.versao());

        entity.setStatus(
                request.status());

        entity.setCurso(curso);

        entity.setPeriodoAtividadeQuadro(
                periodo);

        /*
         * A validação utiliza o ID do próprio quadro para
         * não considerá-lo como duplicado.
         */
        validarQuadroHorarioValidoUseCase
                .validarQuadroAtivoDuplicado(entity);

        /*
         * Valida se o período está ativo.
         */
        validarPeriodoAtividadeQuadroAtivoUseCase
                .executar(entity);

        entity.setUpdatedAt(
                LocalDateTime.now());

        return QuadroHorarioMapper.toResponse(
                repository.save(entity));
    }

    @Transactional
    public void inativar(
            Long id) {

        QuadroHorario entity =
                repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Quadro horário não encontrada com ID: " + id));

        entity.setStatus(
                Status.INATIVO);

        entity.setUpdatedAt(
                LocalDateTime.now());

        repository.save(entity);
    }
}
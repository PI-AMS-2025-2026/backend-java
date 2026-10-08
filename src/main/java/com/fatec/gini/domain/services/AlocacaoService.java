package com.fatec.gini.domain.services;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCargaHorariaMaximaProfessorUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarDuplicidadeAlocacaoLoteUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarSugestaoAutomatica;
import com.fatec.gini.domain.services.usecase.write.AtualizarAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.write.CriarAlocacaoUseCase;
import com.fatec.gini.dto.alocacao.AlocacaoRequest;
import com.fatec.gini.dto.alocacao.AlocacaoResponse;
import com.fatec.gini.dto.alocacao.ValidarCargaHorariaRequest;
import com.fatec.gini.dto.alocacao.ValidarCargaHorariaResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.sugestaoAutomatica.SugestaoRequest;
import com.fatec.gini.dto.sugestaoAutomatica.SugestaoResponse;
import com.fatec.gini.infrastructure.mappers.AlocacaoMapper;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlocacaoService {

    private final AlocacaoRepository repository;

    private final CriarAlocacaoUseCase criarAlocacaoUseCase;

    private final AtualizarAlocacaoUseCase atualizarAlocacaoUseCase;

    private final ValidarDuplicidadeAlocacaoLoteUseCase alocacaoLoteUseCase;

    private final ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

    private final ValidarSugestaoAutomatica validarSugestaoAutomatica;

    private final ValidarCargaHorariaMaximaProfessorUseCase validarCargaHorariaUseCase;

    private final BlocoHorarioRepository blocoHorarioRepository;

    public AlocacaoResponse criar(AlocacaoRequest request) {

        Alocacao entity = AlocacaoMapper.toEntity(request);


        return AlocacaoMapper.toResponse(
                criarAlocacaoUseCase.executar(
                        entity,
                        validarAutorizacaoCurso.usuarioAutenticado().getId()));
    }

    @Transactional
    public List<AlocacaoResponse> criarLote(List<AlocacaoRequest> requests) {
        // 1. Validar duplicidades cruzadas dentro do próprio lote recebido
        alocacaoLoteUseCase.validarDuplicidadesNoLote(requests);

        // 2. Processar cada requisição usando o CriarAlocacaoUseCase existente
        return requests.stream()
                .map(request -> {
                    Alocacao entity = AlocacaoMapper.toEntity(request);

                    Alocacao alocacaoSalva = criarAlocacaoUseCase.executar(
                            entity,
                            validarAutorizacaoCurso.usuarioAutenticado().getId());
                    return AlocacaoMapper.toResponse(alocacaoSalva);
                })
                .toList();
    }

    public AlocacaoResponse atualizar(Long id, AlocacaoRequest request) {

        Alocacao entity = AlocacaoMapper.toEntity(request);

        String justificativaAlteracao = request.justificativaAlteracao();

        return AlocacaoMapper.toResponse(
                atualizarAlocacaoUseCase.executar(
                        id,
                        entity,
                        validarAutorizacaoCurso.usuarioAutenticado().getId(),
                        justificativaAlteracao));
    }

    /*
     * RF06 — Sugestão Automática de Ajuste.
     *
     * Recebe uma tentativa de posicionamento da disciplina
     * na grade de horários.
     *
     * Caso a combinação seja inválida, retorna o motivo
     * e alternativas compatíveis.
     *
     * Nenhuma alocação é persistida neste processo.
     */
    @Transactional(readOnly = true)
    public SugestaoResponse sugerirAlternativas(
            SugestaoRequest request) {

        AlocacaoRequest tentativa = new AlocacaoRequest(
                request.turma().id(),
                request.disciplina().id(),
                request.sala().id(),
                request.professor().id(),
                request.diaSemana(),
                request.horario().id(),
                request.quadroHorario().id(),
                null,
                null);

        Alocacao entity = AlocacaoMapper.toEntity(tentativa);

        String motivo = validarSugestaoAutomatica.identificarMotivo(entity);

        if (motivo == null) {

            return new SugestaoResponse(
                    true,
                    "A combinação informada é válida.",
                    null,
                    List.of());
        }

        List<AlocacaoRequest> sugestoes = validarSugestaoAutomatica
                .executar(entity)
                .stream()
                .map(sugestao -> new AlocacaoRequest(
                        sugestao.getTurma().getId(),
                        sugestao.getDisciplina().getId(),
                        sugestao.getSala().getId(),
                        sugestao.getProfessor().getId(),
                        sugestao.getDiaSemana(),
                        sugestao.getBlocoHorario().getId(),
                        sugestao.getQuadroHorario().getId(),
                        null,
                        null))
                .toList();

        return new SugestaoResponse(
                false,
                "Não é possível realizar a alocação.",
                motivo,
                sugestoes);
    }

    @Transactional(readOnly = true)
    public PageResponse<AlocacaoResponse> listar(
            Long turmaId, Long disciplinaId, Long salaId, Long usuarioId,
            DiaSemana diaSemana, Long horarioId, Long quadroHorarioId, int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);

        var page = repository.buscarPorFiltros(
                turmaId, disciplinaId, salaId, usuarioId,
                diaSemana, horarioId, quadroHorarioId,
                validarAutorizacaoCurso.cursoParaFiltro(null), pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(AlocacaoMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional(readOnly = true)
    public AlocacaoResponse buscarPorId(Long id) {
        Alocacao entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Alocação não encontrada com ID: " + id));
        validarAutorizacaoCurso.validarCurso(entity.getQuadroHorario().getCurso());
        return AlocacaoMapper.toResponse(entity);
    }

    @Transactional
    public void deletar(Long id) {
        Alocacao entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Alocação não encontrada com ID: " + id));
        validarAutorizacaoCurso.validarCurso(entity.getQuadroHorario().getCurso());
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ValidarCargaHorariaResponse validarCargaHorariaSemPersistir(ValidarCargaHorariaRequest request) {
        BlocoHorario blocoHorario = blocoHorarioRepository.findById(request.horario().id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bloco horário não encontrado com ID: " + request.horario().id()));

        Alocacao alocacaoSimulada = new Alocacao();
        alocacaoSimulada.setProfessor(new Professor());
        alocacaoSimulada.getProfessor().setId(request.professor().id());
        alocacaoSimulada.setDiaSemana(request.diaSemana());
        alocacaoSimulada.setBlocoHorario(blocoHorario);

        try {
            validarCargaHorariaUseCase.validar(request.professor().id(), request.diaSemana(),
                    alocacaoSimulada);
            return new ValidarCargaHorariaResponse(true,
                    "Carga horária válida para o professor no dia informado.");
        } catch (BusinessException ex) {
            return new ValidarCargaHorariaResponse(false, ex.getMessage());
        }
    }
}
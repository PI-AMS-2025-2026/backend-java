package com.fatec.gini.domain.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.services.usecase.read.ValidarDuplicidadeAlocacaoLoteUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarSugestaoAutomatica;
import com.fatec.gini.domain.services.usecase.write.AtualizarAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.write.CriarAlocacaoUseCase;
import com.fatec.gini.dto.alocacao.AlocacaoRequest;
import com.fatec.gini.dto.alocacao.AlocacaoResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.sugestaoAutomatica.SugestaoRequest;
import com.fatec.gini.dto.sugestaoAutomatica.SugestaoResponse;
import com.fatec.gini.dto.id.LongDTO;
import com.fatec.gini.infrastructure.mappers.AlocacaoMapper;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlocacaoService {

    private final AlocacaoRepository repository;

    private final CriarAlocacaoUseCase criarAlocacaoUseCase;

    private final AtualizarAlocacaoUseCase atualizarAlocacaoUseCase;

    private final ValidarDuplicidadeAlocacaoLoteUseCase alocacaoLoteUseCase;

    // RF06 - Sugestão Automática de Ajuste
    private final ValidarSugestaoAutomatica validarSugestaoAutomatica;

    public AlocacaoResponse criar(AlocacaoRequest request) {

        Alocacao entity = AlocacaoMapper.toEntity(request);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return AlocacaoMapper.toResponse(
                criarAlocacaoUseCase.executar(
                        entity,
                        request.usuarioAlteracao().id()));
    }

    @Transactional
    public List<AlocacaoResponse> criarLote(List<AlocacaoRequest> requests) {
        // 1. Validar duplicidades cruzadas dentro do próprio lote recebido
        // (Pode chamar a validação descrita no passo 2)
        alocacaoLoteUseCase.validarDuplicidadesNoLote(requests);

        // 2. Processar cada requisição usando o CriarAlocacaoUseCase existente
        return requests.stream()
                .map(request -> {
                    Alocacao entity = AlocacaoMapper.toEntity(request);
                    entity.setCreatedAt(LocalDateTime.now());
                    entity.setUpdatedAt(LocalDateTime.now());

                    // O método executar realiza todas as validações de banco/negócio e persiste
                    Alocacao alocacaoSalva = criarAlocacaoUseCase.executar(
                            entity,
                            request.usuarioAlteracao().id());

                    return AlocacaoMapper.toResponse(alocacaoSalva);
                })
                .toList();
    }

    public AlocacaoResponse atualizar(Long id, AlocacaoRequest request) {

        Alocacao entity = AlocacaoMapper.toEntity(request);

        entity.setUpdatedAt(LocalDateTime.now());
        String justificativaAlteracao = request.justificativaAlteracao();

        return AlocacaoMapper.toResponse(
                atualizarAlocacaoUseCase.executar(
                        id,
                        entity,
                        request.usuarioAlteracao().id(),
                        justificativaAlteracao));
    }

    /**
     * RF06 — Sugestão Automática de Ajuste.
     *
     * Recebe os dados da tentativa de alocação realizada pelo usuário,
     * por exemplo, através do recurso de arrastar e soltar da grade.
     *
     * A tentativa é validada utilizando as regras de negócio existentes.
     * Caso exista algum conflito, são procuradas automaticamente
     * combinações alternativas de sala, dia da semana e bloco de horário.
     *
     * Este método não realiza a persistência da alocação.
     *
     * @param request dados da tentativa de alocação
     * @return resultado da validação e possíveis sugestões de ajuste
     */
    @Transactional(readOnly = true)
    public SugestaoResponse sugerirAlternativas(SugestaoRequest request) {

        AlocacaoRequest tentativa = new AlocacaoRequest(
                request.turma(),
                request.disciplina(),
                request.sala(),
                request.professor(),
                request.diaSemana(),
                request.horario(),
                request.quadroHorario(),
                null,
                request.usuarioAlteracao());

        Alocacao entity = AlocacaoMapper.toEntity(tentativa);

        // Primeiro verifica se a tentativa original é válida.
        String motivo = validarSugestaoAutomatica.identificarMotivo(entity);

        // Se não existe conflito, não é necessário sugerir alteração.
        if (motivo == null) {
            return new SugestaoResponse(
                    true,
                    "A combinação informada é válida.",
                    null,
                    List.of());
        }

        // Caso exista conflito, procura combinações alternativas válidas.
        List<AlocacaoRequest> sugestoes = validarSugestaoAutomatica.executar(entity)
                .stream()
                .map(sugestao -> new AlocacaoRequest(
                        new LongDTO(sugestao.getTurma().getId()),
                        new LongDTO(sugestao.getDisciplina().getId()),
                        new LongDTO(sugestao.getSala().getId()),
                        new LongDTO(sugestao.getProfessor().getId()),
                        sugestao.getDiaSemana(),
                        new LongDTO(sugestao.getBlocoHorario().getId()),
                        new LongDTO(sugestao.getQuadroHorario().getId()),
                        null,
                        request.usuarioAlteracao()))
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
                diaSemana, horarioId, quadroHorarioId, pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(AlocacaoMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional(readOnly = true)
    public AlocacaoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(AlocacaoMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Alocação não encontrada com ID: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Alocação não encontrada com ID: " + id);
        }
        repository.deleteById(id);
    }
}
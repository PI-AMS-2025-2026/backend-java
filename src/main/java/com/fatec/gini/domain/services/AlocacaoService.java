package com.fatec.gini.domain.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Professor;
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

    private final ValidarCargaHorariaMaximaProfessorUseCase validarCargaHorariaUseCase;

    private final BlocoHorarioRepository blocoHorarioRepository;

    public AlocacaoResponse criar(AlocacaoRequest request) {

    public AlocacaoResponse criar(AlocacaoRequest request) {

        Alocacao entity = AlocacaoMapper.toEntity(request);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return AlocacaoMapper.toResponse(
                criarAlocacaoUseCase.executar(
                        entity,
                        request.usuarioAlteracaoId())); // Alteração: utiliza diretamente o ID do usuário.
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
                            request.usuarioAlteracaoId()); // Alteração: utiliza diretamente o ID do usuário.
                    return AlocacaoMapper.toResponse(alocacaoSalva);
                })
                .toList();
    }

    public AlocacaoResponse atualizar(Long id, AlocacaoRequest request) {

        Alocacao entity = AlocacaoMapper.toEntity(request);

        entity.setUpdatedAt(LocalDateTime.now());

        String justificativaAlteracao =
                request.justificativaAlteracao();

        return AlocacaoMapper.toResponse(
                atualizarAlocacaoUseCase.executar(
                        id,
                        entity,
                        request.usuarioAlteracao().id(),
                        justificativaAlteracao
                )
        );
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

        /*
         * Cria uma AlocacaoRequest temporária apenas para
         * reutilizar o AlocacaoMapper existente.
         */
        AlocacaoRequest tentativa =
                new AlocacaoRequest(
                        request.turma(),
                        request.disciplina(),
                        request.sala(),
                        request.professor(),
                        request.diaSemana(),
                        request.horario(),
                        request.quadroHorario(),
                        null,
                        null
                );

        /*
         * Cria uma entidade temporária.
         *
         * Essa entidade não é salva no banco.
         */
        Alocacao entity =
                AlocacaoMapper.toEntity(tentativa);

        /*
         * Primeiro verifica se a combinação escolhida
         * pelo usuário já é válida.
         */
        String motivo =
                validarSugestaoAutomatica
                        .identificarMotivo(entity);

        /*
         * Se não existe conflito, não há necessidade
         * de gerar sugestões.
         */
        if (motivo == null) {

            return new SugestaoResponse(
                    true,
                    "A combinação informada é válida.",
                    null,
                    List.of()
            );
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
                        validarCargaHorariaUseCase.validar(request.professor().id(), request.diaSemana(), alocacaoSimulada);
                        return new ValidarCargaHorariaResponse(true, "Carga horária válida para o professor no dia informado.");
                } catch (BusinessException ex) {
                        return new ValidarCargaHorariaResponse(false, ex.getMessage());
                }
        }

        @Transactional
        public void deletar(Long id) {
                if (!repository.existsById(id)) {
                        throw new EntityNotFoundException(
                                        "Alocação não encontrada com ID: " + id);
                }
                repository.deleteById(id);
        }
        repository.deleteById(id);
    }
}

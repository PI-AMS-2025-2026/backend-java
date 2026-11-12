package com.fatec.gini.domain.services;

import java.util.List;
import java.util.Objects;

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
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;
import com.fatec.gini.web.exception.BusinessException;
import com.fatec.gini.web.exception.ParameterException;

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

    private final ProfessorRepository professorRepository;

    private final DisponibilidadeProfessorRepository disponibilidadeProfessorRepository;

    private final TurmaRepository turmaRepository;

    private final DisciplinaRepository disciplinaRepository;

    private final SalaRepository salaRepository;

    private final QuadroHorarioRepository quadroHorarioRepository;

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

        return AlocacaoMapper.toResponse(
                atualizarAlocacaoUseCase.executar(
                        id,
                        entity,
                        validarAutorizacaoCurso.usuarioAutenticado().getId()));
    }

    /*
     * RF06 — Sugestão Automática de Ajuste.
     *
     * Recebe uma tentativa de posicionamento da disciplina
     * no quadro de horários.
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
                null);

        Alocacao entity = AlocacaoMapper.toEntity(tentativa);

        // AlocacaoMapper builds ID-only references for write requests. Suggestions
        // need the related values (capacity, room type, course and period) to run
        // the same validation rules as create/update.
        entity.setTurma(turmaRepository.findById(request.turma().id())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada.")));
        entity.setDisciplina(disciplinaRepository.findById(request.disciplina().id())
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada.")));
        entity.setSala(salaRepository.findById(request.sala().id())
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada.")));
        entity.setProfessor(professorRepository.findById(request.professor().id())
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado.")));
        entity.setBlocoHorario(blocoHorarioRepository.findById(request.horario().id())
                .orElseThrow(() -> new EntityNotFoundException("Bloco de horário não encontrado.")));
        entity.setQuadroHorario(quadroHorarioRepository.findById(request.quadroHorario().id())
                .orElseThrow(() -> new EntityNotFoundException("Quadro horário não encontrado.")));
        if (request.alocacaoId() != null) {
            Alocacao alocacaoAtual = repository.findById(request.alocacaoId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Alocação não encontrada com ID: " + request.alocacaoId()));
            validarAutorizacaoCurso.validarCurso(alocacaoAtual.getQuadroHorario().getCurso());
            if (!Objects.equals(alocacaoAtual.getTurma().getId(), request.turma().id())
                    || !Objects.equals(alocacaoAtual.getQuadroHorario().getId(), request.quadroHorario().id())) {
                throw new ParameterException(
                        "A alocação informada não pertence à turma e ao quadro horário enviados.");
            }
            entity.setId(alocacaoAtual.getId());
        }

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
                        null))
                .toList();

        String motivoFormatado = motivo.endsWith(".") ? motivo : motivo + ".";
        String mensagem = sugestoes.isEmpty()
                ? motivoFormatado + " Não foram encontrados horários alternativos que atendam à disponibilidade, carga horária e demais regras atuais."
                : motivoFormatado + " Foram encontradas alternativas compatíveis; selecione uma opção sugerida.";

        return new SugestaoResponse(false, mensagem, motivo, sugestoes);
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
                page.getTotalElements(),
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
        if (!professorRepository.existsById(request.professor().id())) {
            throw new EntityNotFoundException(
                    "Professor não encontrado com ID: " + request.professor().id());
        }

        BlocoHorario blocoHorario = blocoHorarioRepository.findById(request.horario().id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bloco horário não encontrado com ID: " + request.horario().id()));

        validarBlocoHorario(blocoHorario);

        Alocacao alocacaoSimulada = new Alocacao();
        alocacaoSimulada.setId(request.alocacaoId());
        alocacaoSimulada.setProfessor(new Professor());
        alocacaoSimulada.getProfessor().setId(request.professor().id());
        alocacaoSimulada.setDiaSemana(request.diaSemana());
        alocacaoSimulada.setBlocoHorario(blocoHorario);

        try {
            if (!disponibilidadeProfessorRepository.verificarDisponibilidadeProfessor(
                    request.professor().id(), request.diaSemana(), blocoHorario.getId())) {
                return new ValidarCargaHorariaResponse(false,
                        "Professor não possui disponibilidade para o dia e horário informado.");
            }
            validarCargaHorariaUseCase.validar(request.professor().id(), request.diaSemana(),
                    alocacaoSimulada);
            return new ValidarCargaHorariaResponse(true,
                    "Carga horária válida para o professor no dia informado.");
        } catch (BusinessException ex) {
            return new ValidarCargaHorariaResponse(false, ex.getMessage());
        }
    }

    private void validarBlocoHorario(BlocoHorario blocoHorario) {
        if (blocoHorario.getHoraInicio() == null || blocoHorario.getHoraFim() == null
                || blocoHorario.getDuracao() == null) {
            throw new ParameterException(
                    "O bloco de horário informado está incompleto: início, fim e duração são obrigatórios.");
        }

        if (!blocoHorario.getHoraFim().isAfter(blocoHorario.getHoraInicio())
                || blocoHorario.getDuracao() <= 0) {
            throw new ParameterException("O bloco de horário informado possui duração ou intervalo inválido.");
        }
    }
}

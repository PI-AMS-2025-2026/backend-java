package com.fatec.horario.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.domain.services.usecase.write.AlteracaoAlocacaoUseCase;
import com.fatec.horario.domain.services.usecase.write.DuplicidadeAlocacaoUseCase;
import com.fatec.horario.dto.alocacao.AlocacaoRequest;
import com.fatec.horario.dto.alocacao.AlocacaoResponse;
import com.fatec.horario.infrastructure.mappers.AlocacaoMapper;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.infrastructure.repositories.DiaSemanaRepository;
import com.fatec.horario.infrastructure.repositories.DisciplinaRepository;
import com.fatec.horario.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.horario.infrastructure.repositories.HorarioRepository;
import com.fatec.horario.infrastructure.repositories.SalaRepository;
import com.fatec.horario.infrastructure.repositories.TurmaRepository;
import com.fatec.horario.infrastructure.repositories.UsuarioRepository;
import com.fatec.horario.web.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AlocacaoService {

    @Autowired
    private AlocacaoRepository repository;
    @Autowired
    private TurmaRepository turmaRepository;
    @Autowired
    private DisciplinaRepository disciplinaRepository;
    @Autowired
    private SalaRepository salaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DiaSemanaRepository diaSemanaRepository;
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private GradeHorariaRepository gradeHorariaRepository;
    @Autowired
    private AlteracaoAlocacaoUseCase alteracaoAlocacaoUseCase;
    @Autowired
    private DuplicidadeAlocacaoUseCase duplicidadeAlocacaoUseCase;

    @Transactional
    public AlocacaoResponse criar(AlocacaoRequest request) {
        Alocacao entity = montarAlocacao(request);

        duplicidadeAlocacaoUseCase.validarNaoExisteDuplicidadeParaCriacao(entity);

        return AlocacaoMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public AlocacaoResponse atualizar(Long id, AlocacaoRequest request) {
        Alocacao entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alocação não encontrada com ID: " + id));

        validarJustificativaAlteracao(request);
        validarUsuarioAlteracao(request);

        var usuarioAlteracaoRequest = request.usuarioAlteracao();
        if (usuarioAlteracaoRequest == null) {
            throw new BusinessException("Usuário de alteração é obrigatório.");
        }

        Turma novaTurma = buscarTurmaPorId(request.turma().id());
        Disciplina novaDisciplina = buscarDisciplinaPorId(request.disciplina().id());
        Sala novaSala = buscarSalaPorId(request.sala().id());
        Usuario novoUsuario = buscarUsuarioPorId(request.usuario().id());
        Usuario usuarioAlteracao = buscarUsuarioPorId(usuarioAlteracaoRequest.id());
        DiaSemana novoDiaSemana = buscarDiaSemanaPorId(request.diaSemana().id());
        Horario novoHorario = buscarHorarioPorId(request.horario().id());
        GradeHoraria novaGradeHoraria = buscarGradeHorariaPorId(request.gradeHoraria().id());

        Alocacao alocacaoAtualizada = new Alocacao(
            id,
            novaTurma,
            novaDisciplina,
            novaSala,
            novoUsuario,
            novoDiaSemana,
            novoHorario,
            novaGradeHoraria);

        duplicidadeAlocacaoUseCase.validarNaoExisteDuplicidadeParaAtualizacao(alocacaoAtualizada);

        alteracaoAlocacaoUseCase.executarCasoUso(
                entity,
                novaTurma,
                novaDisciplina,
                novaSala,
                novoUsuario,
                usuarioAlteracao,
                novoDiaSemana,
                novoHorario,
                novaGradeHoraria,
                request.justificativaAlteracao());

            return AlocacaoMapper.toResponse(repository.save(alocacaoAtualizada));
    }

    @Transactional(readOnly = true)
    public Page<AlocacaoResponse> listar(
            Long turmaId, Long disciplinaId, Long salaId, Long usuarioId,
            Long diaSemanaId, Long horarioId, Long gradeId, int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);
        var pageAlocacao = repository.buscarPorFiltros(
                turmaId, disciplinaId, salaId, usuarioId, diaSemanaId, horarioId, gradeId, pageRequest);

        return pageAlocacao.map(AlocacaoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AlocacaoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(AlocacaoMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Alocação não encontrada com ID: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Alocação não encontrada com ID: " + id);
        }
        repository.deleteById(id);
    }

    // Validações

    private void validarJustificativaAlteracao(AlocacaoRequest request) {
        if (!StringUtils.hasText(request.justificativaAlteracao())) {
            throw new BusinessException("A justificativa da alteração é obrigatória para atualizar a alocação.");
        }
    }

    private void validarUsuarioAlteracao(AlocacaoRequest request) {
        if (request.usuarioAlteracao() == null) {
            throw new BusinessException(
                    "O usuário responsável pela alteração é obrigatório para atualizar a alocação.");
        }
    }

    // Buscar entidades da relação
    private Turma buscarTurmaPorId(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));
    }

    private Disciplina buscarDisciplinaPorId(Long id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada com ID: " + id));
    }

    private Sala buscarSalaPorId(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada com ID: " + id));
    }

    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    private DiaSemana buscarDiaSemanaPorId(Long id) {
        return diaSemanaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dia da semana não encontrado com ID: " + id));
    }

    private Horario buscarHorarioPorId(Long id) {
        return horarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado com ID: " + id));
    }

    private GradeHoraria buscarGradeHorariaPorId(Long id) {
        return gradeHorariaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade horária não encontrada com ID: " + id));
    }

    private Alocacao montarAlocacao(AlocacaoRequest request) {
        return new Alocacao(
                buscarTurmaPorId(request.turma().id()),
                buscarDisciplinaPorId(request.disciplina().id()),
                buscarSalaPorId(request.sala().id()),
                buscarUsuarioPorId(request.usuario().id()),
                buscarDiaSemanaPorId(request.diaSemana().id()),
                buscarHorarioPorId(request.horario().id()),
                buscarGradeHorariaPorId(request.gradeHoraria().id()));
    }
}
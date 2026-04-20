package com.fatec.horario.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.domain.entities.Usuario;
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

    @Transactional
    public AlocacaoResponse criar(AlocacaoRequest request) {
        validarRegrasDeNegocio(null, request);

        Alocacao entity = AlocacaoMapper.toEntity(request);
        entity.setTurma(buscarTurmaPorId(request.turma().id()));
        entity.setDisciplina(buscarDisciplinaPorId(request.disciplina().id()));
        entity.setSala(buscarSalaPorId(request.sala().id()));
        entity.setUsuario(buscarUsuarioPorId(request.usuario().id()));
        entity.setDiaSemana(buscarDiaSemanaPorId(request.diaSemana().id()));
        entity.setHorario(buscarHorarioPorId(request.horario().id()));
        entity.setGradeHoraria(buscarGradeHorariaPorId(request.gradeHoraria().id()));

        return AlocacaoMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public AlocacaoResponse atualizar(Long id, AlocacaoRequest request) {
        Alocacao entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alocação não encontrada com ID: " + id));

        validarRegrasDeNegocio(id, request);

        entity.setTurma(buscarTurmaPorId(request.turma().id()));
        entity.setDisciplina(buscarDisciplinaPorId(request.disciplina().id()));
        entity.setSala(buscarSalaPorId(request.sala().id()));
        entity.setUsuario(buscarUsuarioPorId(request.usuario().id()));
        entity.setDiaSemana(buscarDiaSemanaPorId(request.diaSemana().id()));
        entity.setHorario(buscarHorarioPorId(request.horario().id()));
        entity.setGradeHoraria(buscarGradeHorariaPorId(request.gradeHoraria().id()));

        return AlocacaoMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<AlocacaoResponse> listar(
            Long turmaId, Long disciplinaId, Long salaId, Long usuarioId,
            Long diaSemanaId, Long horarioId, Long gradeId, Pageable pageable) {

        var pageAlocacao = repository.buscarPorFiltros(
                turmaId, disciplinaId, salaId, usuarioId, diaSemanaId, horarioId, gradeId, pageable);

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

    //TODO: Passar validações para um service específico de regras de negócio

    private void validarRegrasDeNegocio(Long idAtual, AlocacaoRequest req) {

        boolean salaOcupada = repository.existsBySalaIdAndDiaSemanaIdAndHorarioId(
                req.sala().id(), req.diaSemana().id(), req.horario().id());

        if (salaOcupada && isConflitoSalaComOutraAlocacao(idAtual, req)) {
            throw new BusinessException("A sala informada já está ocupada neste horário.");
        }

        boolean professorOcupado = repository.existsByUsuarioIdAndDiaSemanaIdAndHorarioId(
                req.usuario().id(), req.diaSemana().id(), req.horario().id());

        if (professorOcupado && isConflitoProfessorComOutraAlocacao(idAtual, req)) {
            throw new BusinessException("O professor já possui uma alocação neste horário.");
        }

        boolean possuiDisponibilidade = repository.verificarDisponibilidadeProfessor(
                req.usuario().id(),
                req.diaSemana().id(),
                req.horario().id());

        if (!possuiDisponibilidade) {
            throw new BusinessException("O professor não possui disponibilidade cadastrada para este horário.");
        }
    }

    private boolean isConflitoSalaComOutraAlocacao(Long idAtual, AlocacaoRequest req) {
        if (idAtual == null) {
            return true;
        }

        Alocacao alocacaoAtual = repository.findById(idAtual)
                .orElseThrow(() -> new EntityNotFoundException("Alocação não encontrada com ID: " + idAtual));

        boolean mesmaCombinacaoAtual = alocacaoAtual.getSala().getId().equals(req.sala().id())
                && alocacaoAtual.getDiaSemana().getId().equals(req.diaSemana().id())
                && alocacaoAtual.getHorario().getId().equals(req.horario().id());

        return !mesmaCombinacaoAtual;
    }

    private boolean isConflitoProfessorComOutraAlocacao(Long idAtual, AlocacaoRequest req) {
        if (idAtual == null) {
            return true;
        }

        Alocacao alocacaoAtual = repository.findById(idAtual)
                .orElseThrow(() -> new EntityNotFoundException("Alocação não encontrada com ID: " + idAtual));

        boolean mesmaCombinacaoAtual = alocacaoAtual.getUsuario().getId().equals(req.usuario().id())
                && alocacaoAtual.getDiaSemana().getId().equals(req.diaSemana().id())
                && alocacaoAtual.getHorario().getId().equals(req.horario().id());

        return !mesmaCombinacaoAtual;
    }

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
}
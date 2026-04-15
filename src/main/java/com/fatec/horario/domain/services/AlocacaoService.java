package com.fatec.horario.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.*;
import com.fatec.horario.dto.alocacao.AlocacaoRequest;
import com.fatec.horario.dto.alocacao.AlocacaoResponse;
import com.fatec.horario.infrastructure.mappers.AlocacaoMapper;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AlocacaoService {

    @Autowired
    private AlocacaoRepository repository;

    @Transactional
    public AlocacaoResponse criar(AlocacaoRequest request) {
        // Validação de regras de negócio antes de salvar
        validarRegrasDeNegocio(null, request);

        Alocacao entity = AlocacaoMapper.toEntity(request);
        return AlocacaoMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public AlocacaoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(AlocacaoMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Alocação não encontrada com ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<AlocacaoResponse> listar(
            Long turmaId, Long disciplinaId, Long salaId, Long usuarioId,
            Long diaSemanaId, Long horarioId, Long gradeId, Pageable pageable) {

        Page<Alocacao> pageAlocacao = repository.buscarPorFiltros(
                turmaId, disciplinaId, salaId, usuarioId, diaSemanaId, horarioId, gradeId, pageable);

        return pageAlocacao.map(AlocacaoMapper::toResponse);
    }

    @Transactional
    public AlocacaoResponse atualizar(Long id, AlocacaoRequest request) {
        Alocacao entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alocação não encontrada com ID: " + id));

        // Valida conflitos ignorando o próprio ID da alocação que está sendo editada
        validarRegrasDeNegocio(id, request);

        entity.setTurma(request.turma());
        entity.setDisciplina(request.disciplina());
        entity.setSala(request.sala());
        entity.setUsuario(request.usuario());
        entity.setDiaSemana(request.diaSemana());
        entity.setHorario(request.horario());
        entity.setGradeHoraria(request.gradeHoraria());

        return AlocacaoMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Alocação não encontrada com ID: " + id);
        }
        repository.deleteById(id);
    }

   
    private void validarRegrasDeNegocio(Long idAtual, AlocacaoRequest req) {

        // 1. Não permitir conflito de sala no mesmo horário
        boolean salaOcupada = repository.existsBySalaIdAndDiaSemanaIdAndHorarioId(
                req.sala().getId(), req.diaSemana().getId(), req.horario().getId());

       
        if (salaOcupada && idAtual == null) {
            throw new RuntimeException("A sala informada já está ocupada neste horário.");
        }

        // 2. Não permitir conflito de professor no mesmo horário
        boolean professorOcupado = repository.existsByUsuarioIdAndDiaSemanaIdAndHorarioId(
                req.usuario().getId(), req.diaSemana().getId(), req.horario().getId());

        if (professorOcupado && idAtual == null) {
            throw new RuntimeException("O professor já possui uma alocação neste horário.");
        }

        // 3. Respeitar disponibilidade do professor
        boolean possuiDisponibilidade = repository.verificarDisponibilidadeProfessor(
                req.usuario().getId(),
                req.diaSemana().getId(),
                req.horario().getId());

        if (!possuiDisponibilidade) {
            throw new RuntimeException("O professor não possui disponibilidade cadastrada para este horário.");
        }
    }
}
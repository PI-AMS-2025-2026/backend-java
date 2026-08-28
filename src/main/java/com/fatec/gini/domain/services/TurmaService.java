package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarTurmaSemVinculosUseCase;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.turma.TurmaRequest;
import com.fatec.gini.dto.turma.TurmaResponse;
import com.fatec.gini.infrastructure.mappers.TurmaMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;
import com.fatec.gini.web.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository repository;

    private final CursoRepository cursoRepository;

    private final ValidarTurmaSemVinculosUseCase validarTurmaSemVinculos;

    @Transactional
    public TurmaResponse criar(TurmaRequest request) {

        Curso curso = cursoRepository.findById(request.curso().id())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Curso não encontrado com ID: "
                                        + request.curso().id()));

        // Regra: não permite turma em curso inativo.
        if (curso.getStatus() != Status.ATIVO) {
            throw new BusinessException(
                    "Não é possível cadastrar turma em curso inativo.");
        }

        // Regra: período deve ser compatível com a duração do curso.
        if (request.periodo() > curso.getDuracao()) {
            throw new BusinessException(
                    "O período da turma é incompatível com a duração do curso.");
        }

        // Regra: não permite duas turmas iguais
        // no mesmo curso, ano e período.
        if (repository.existsByCursoIdAndAnoAndPeriodo(
                curso.getId(),
                request.ano(),
                request.periodo())) {

            throw new BusinessException(
                    "Já existe uma turma para este curso, ano e período.");
        }

        Turma entity = TurmaMapper.toEntity(request);

        entity.setCurso(curso);

        entity.setCodigo(
                request.periodo() + "/" + request.ano());

        entity.setCreatedAt(LocalDateTime.now());

        entity.setUpdatedAt(LocalDateTime.now());

        return TurmaMapper.toResponse(
                repository.save(entity));
    }

    @Transactional(readOnly = true)
    public TurmaResponse buscarPorId(Long id) {

        Turma entity = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Turma não encontrada com ID: " + id));

        return TurmaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public PageResponse<TurmaResponse> listar(
            Long idCurso,
            Integer ano,
            Integer periodo,
            String codigo,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);

        var page = repository.buscarPorFiltros(
                idCurso,
                ano,
                periodo,
                codigo,
                pageRequest);

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(TurmaMapper::toResponse)
                        .toList(),

                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public TurmaResponse atualizar(
            Long id,
            TurmaRequest request) {

        Turma entity = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Turma não encontrada com ID: " + id));

        Curso curso = cursoRepository.findById(request.curso().id())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Curso não encontrado com ID: "
                                        + request.curso().id()));

        // Regra: não permite vincular turma a curso inativo.
        if (curso.getStatus() != Status.ATIVO) {
            throw new BusinessException(
                    "Não é possível vincular a turma a um curso inativo.");
        }

        // Regra: período deve ser compatível com a duração.
        if (request.periodo() > curso.getDuracao()) {
            throw new BusinessException(
                    "O período da turma é incompatível com a duração do curso.");
        }

        // Regra: impede duplicidade na atualização,
        // ignorando a própria turma.
        if (repository.existsByCursoIdAndAnoAndPeriodoAndIdNot(
                curso.getId(),
                request.ano(),
                request.periodo(),
                id)) {

            throw new BusinessException(
                    "Já existe uma turma para este curso, ano e período.");
        }

        entity.setCodigo(
                request.periodo() + "/" + request.ano());

        entity.setAno(request.ano());

        entity.setPeriodo(request.periodo());

        entity.setNumeroAlunos(
                request.numeroAlunos());

        entity.setCurso(curso);

        entity.setUpdatedAt(LocalDateTime.now());

        return TurmaMapper.toResponse(
                repository.save(entity));
    }

    @Transactional
    public void deletar(Long id) {

        Turma turma = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Turma não encontrada com ID: " + id));

        // Regra: não permite excluir turma
        // vinculada a curso ativo.
        if (turma.getCurso() != null
                && turma.getCurso().getStatus() == Status.ATIVO) {

            throw new BusinessException(
                    "Não é possível excluir turma vinculada a curso ativo.");
        }

        // Regra já existente:
        // não permite excluir turma que possui alocações.
        validarTurmaSemVinculos.validar(id);

        repository.deleteById(id);
    }
}
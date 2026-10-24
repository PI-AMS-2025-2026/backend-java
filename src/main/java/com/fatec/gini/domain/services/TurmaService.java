
package com.fatec.gini.domain.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;
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
    private final ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

    @Transactional
    public TurmaResponse criar(TurmaRequest request) {

        validarAno(request.ano());

        Curso curso = cursoRepository.findById(request.curso().id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + request.curso().id()));

        validarAutorizacaoCurso.validarCurso(curso);

        if (curso.getStatus() != Status.ATIVO) {
            throw new BusinessException(
                    "Não é possível cadastrar turma em curso inativo.");
        }

        validarPeriodo(request, curso);

        if (repository.existsByCursoIdAndAnoAndPeriodo(
                curso.getId(), request.ano(), request.periodo())) {
            throw new BusinessException(
                    "Já existe uma turma para este curso, ano e período.");
        }

        Turma entity = TurmaMapper.toEntity(request);
        entity.setCurso(curso);
        entity.setStatus(Status.ATIVO);
        entity.setCodigo(request.periodo() + "/" + request.ano());

        return TurmaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public TurmaResponse buscarPorId(Long id) {

        Turma entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Turma não encontrada com ID: " + id));

        validarAutorizacaoCurso.validarCurso(entity.getCurso());

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
                validarAutorizacaoCurso.cursoParaFiltro(idCurso),
                ano,
                periodo,
                codigo,
                pageRequest);

        return new PageResponse<>(
                page.getContent().stream()
                        .map(TurmaMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public TurmaResponse atualizar(Long id, TurmaRequest request) {

        validarAno(request.ano());

        Turma entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Turma não encontrada com ID: " + id));

        Curso curso = cursoRepository.findById(request.curso().id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + request.curso().id()));

        validarAutorizacaoCurso.validarCurso(entity.getCurso());
        validarAutorizacaoCurso.validarCurso(curso);

        if (curso.getStatus() != Status.ATIVO) {
            throw new BusinessException(
                    "Não é possível vincular a turma a um curso inativo.");
        }

        validarPeriodo(request, curso);

        if (repository.existsByCursoIdAndAnoAndPeriodoAndIdNot(
                curso.getId(), request.ano(), request.periodo(), id)) {
            throw new BusinessException(
                    "Já existe uma turma para este curso, ano e período.");
        }

        entity.setCodigo(request.periodo() + "/" + request.ano());
        entity.setAno(request.ano());
        entity.setPeriodo(request.periodo());
        entity.setNumeroAlunos(request.numeroAlunos());
        entity.setCurso(curso);

        return TurmaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public TurmaResponse inativar(Long id) {

        Turma entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Turma não encontrada com ID: " + id));

        validarAutorizacaoCurso.validarCurso(entity.getCurso());

        entity.setStatus(Status.INATIVO);

        return TurmaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(Long id) {

        Turma entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Turma não encontrada com ID: " + id));

        validarAutorizacaoCurso.validarCurso(entity.getCurso());

        if (entity.getStatus() != Status.INATIVO) {
            throw new BusinessException(
                    "Não é possível excluir uma turma ativa. Inative a turma antes de excluí-la.");
        }

        validarTurmaSemVinculos.validar(id);

        repository.delete(entity);
    }

    private void validarAno(Integer ano) {
        if (ano == null || ano <= 2008 || ano > 9999) {
            throw new BusinessException(
                    "O ano da turma deve ser maior que 2008 e possuir no máximo 4 dígitos.");
        }
    }

    private void validarPeriodo(TurmaRequest request, Curso curso) {
        if (request.periodo() > curso.getDuracao()) {
            throw new BusinessException(
                    "O período da turma é incompatível com a duração do curso.");
        }
    }
}

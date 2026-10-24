
package com.fatec.gini.domain.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;
import com.fatec.gini.dto.curso.CursoRequest;
import com.fatec.gini.dto.curso.CursoResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.infrastructure.mappers.CursoMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository repository;
    private final TurmaRepository turmaRepository;
    private final ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

    @Transactional
    public CursoResponse criar(CursoRequest dto) {

        validarAutorizacaoCurso.validarAdministrador();

        String periodicidade = normalizarPeriodicidade(dto.periodicidade());
        validarDuracao(periodicidade, dto.duracao());

        if (repository.existsByNomeIgnoreCaseAndPeriodicidadeIgnoreCaseAndDuracao(
                dto.nome(), periodicidade, dto.duracao())) {
            throw new IllegalArgumentException(
                    "Já existe um curso com o mesmo nome, periodicidade e duração");
        }

        Curso entity = CursoMapper.toEntity(dto);
        entity.setPeriodicidade(periodicidade);

        return CursoMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public CursoResponse buscarPorId(Long id) {

        Curso curso = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + id));

        validarAutorizacaoCurso.validarCurso(curso);

        return CursoMapper.toResponse(curso);
    }

    @Transactional(readOnly = true)
    public PageResponse<CursoResponse> listar(
            String nome,
            String periodicidade,
            Status status,
            Integer duracao,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);

        var page = repository.buscarPorFiltros(
                nome,
                periodicidade,
                status,
                duracao,
                validarAutorizacaoCurso.cursoParaFiltro(null),
                pageRequest);

        return new PageResponse<>(
                page.getContent().stream()
                        .map(CursoMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public CursoResponse atualizar(Long id, CursoRequest request) {

        validarAutorizacaoCurso.validarAdministrador();

        Curso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + id));

        String periodicidade = normalizarPeriodicidade(request.periodicidade());
        validarDuracao(periodicidade, request.duracao());

        boolean cursoDuplicado =
                repository.existsByNomeIgnoreCaseAndPeriodicidadeIgnoreCaseAndDuracao(
                        request.nome(), periodicidade, request.duracao());

        boolean mesmoCurso =
                entity.getNome().equalsIgnoreCase(request.nome())
                && entity.getPeriodicidade().equalsIgnoreCase(periodicidade)
                && entity.getDuracao().equals(request.duracao());

        if (cursoDuplicado && !mesmoCurso) {
            throw new IllegalArgumentException(
                    "Já existe um curso com o mesmo nome, periodicidade e duração");
        }

        entity.setNome(request.nome());
        entity.setPeriodicidade(periodicidade);
        entity.setStatus(request.status());
        entity.setDuracao(request.duracao());

        // Se o curso for atualizado para inativo, inativa suas turmas.
        if (entity.getStatus() == Status.INATIVO) {
            inativarTurmasDoCurso(entity.getId());
        }

        return CursoMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void inativar(Long id) {

        validarAutorizacaoCurso.validarAdministrador();

        Curso entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + id));

        entity.setStatus(Status.INATIVO);

        repository.save(entity);
        inativarTurmasDoCurso(entity.getId());
    }

    private void inativarTurmasDoCurso(Long cursoId) {

        for (Turma turma : turmaRepository
                .findByCursoIdOrderByAnoAscPeriodoAscCodigoAsc(cursoId)) {

            if (turma.getStatus() != Status.INATIVO) {
                turma.setStatus(Status.INATIVO);
            }
        }

        // Persiste as mudanças de status das turmas.
        turmaRepository.saveAll(
                turmaRepository.findByCursoIdOrderByAnoAscPeriodoAscCodigoAsc(cursoId));
    }

    private String normalizarPeriodicidade(String periodicidade) {

        if (periodicidade == null) {
            throw new IllegalArgumentException("Periodicidade é obrigatória");
        }

        String valor = periodicidade.trim();

        if (valor.equalsIgnoreCase("Anual")) {
            return "Anual";
        }

        if (valor.equalsIgnoreCase("Semestral")) {
            return "Semestral";
        }

        throw new IllegalArgumentException(
                "Periodicidade deve ser 'Semestral' ou 'Anual'");
    }

    private void validarDuracao(String periodicidade, Integer duracao) {

        if (duracao == null) {
            throw new IllegalArgumentException("Duração é obrigatória");
        }

        if (periodicidade.equals("Anual")) {
            if (duracao <= 1 || duracao > 4) {
                throw new IllegalArgumentException(
                        "Para periodicidade Anual, a duração deve ser maior que 1 e menor ou igual a 4");
            }
        }

        if (periodicidade.equals("Semestral")) {
            if (duracao < 4 || duracao > 10) {
                throw new IllegalArgumentException(
                        "Para periodicidade Semestral, a duração deve estar entre 4 e 10");
            }
        }
    }
}

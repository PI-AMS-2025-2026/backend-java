package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.services.usecase.read.ValidarTurmaSemVinculosUseCase;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.turma.TurmaRequest;
import com.fatec.gini.dto.turma.TurmaResponse;
import com.fatec.gini.infrastructure.mappers.TurmaMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;

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
        Turma entity = TurmaMapper.toEntity(request);

        Curso curso = cursoRepository.findById(request.curso().id())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + request.curso().id()));
        entity.setCurso(curso);
        entity.setCodigo(request.periodo() + "/" + request.ano());

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return TurmaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public TurmaResponse buscarPorId(long id) {
        Turma entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));
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
        var page = repository.buscarPorFiltros(idCurso, ano, periodo, codigo, pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(TurmaMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public TurmaResponse atualizar(long id, TurmaRequest request) {
        Turma entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));

        Curso curso = cursoRepository.findById(request.curso().id())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + request.curso().id()));

        entity.setCodigo(request.periodo() + "/" + request.ano());

        entity.setAno(request.ano());
        entity.setPeriodo(request.periodo());
        entity.setNumeroAlunos(request.numeroAlunos());
        entity.setCurso(curso);

        entity.setUpdatedAt(LocalDateTime.now());
        return TurmaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Turma não encontrada com ID: " + id);
        }
        validarTurmaSemVinculos.validar(id);
        repository.deleteById(id);
    }
}
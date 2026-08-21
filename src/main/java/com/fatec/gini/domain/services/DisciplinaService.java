package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.TipoSala;
import com.fatec.gini.domain.services.usecase.read.ValidarDisciplinaSemVinculosUseCase;
import com.fatec.gini.dto.disciplina.DisciplinaRequest;
import com.fatec.gini.dto.disciplina.DisciplinaResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.infrastructure.mappers.DisciplinaMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.TipoSalaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository repository;

    private final CursoRepository cursoRepository;

    private final TipoSalaRepository tipoSalaRepository;

    private final ValidarDisciplinaSemVinculosUseCase validarDisciplinaSemVinculos;

    @Transactional
    public DisciplinaResponse criar(DisciplinaRequest request) {

        Disciplina entity = DisciplinaMapper.toEntity(request);

        // Alteração: utiliza diretamente o ID do curso recebido no payload.
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new EntityNotFoundException(
                "Curso não encontrado com ID: " + request.cursoId()));

// Alteração: utiliza diretamente o ID do tipo de sala recebido no payload.
        TipoSala tipoSala = tipoSalaRepository.findById(request.tipoSalaId())
                .orElseThrow(() -> new EntityNotFoundException(
                "Tipo de sala não encontrado com ID: " + request.tipoSalaId()));

        entity.setCurso(curso);
        entity.setTipoSala(tipoSala);

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return DisciplinaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public DisciplinaResponse buscarPorId(Long id) {

        Disciplina entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                "Disciplina não encontrada com ID: " + id));

        return DisciplinaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public PageResponse<DisciplinaResponse> listar(
            String nome,
            Long cursoId,
            Long tipoSalaId,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);

        var page = repository.findByFiltros(
                nome,
                cursoId,
                tipoSalaId,
                pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(DisciplinaMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public DisciplinaResponse atualizar(Long id, DisciplinaRequest request) {

        Disciplina entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                "Disciplina não encontrada com ID: " + id));

        // Alteração: utiliza diretamente o ID do curso recebido no payload.
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new EntityNotFoundException(
                "Curso não encontrado com ID: " + request.cursoId()));

        // Alteração: utiliza diretamente o ID do tipo de sala recebido no payload.
        TipoSala tipoSala = tipoSalaRepository.findById(request.tipoSalaId())
                .orElseThrow(() -> new EntityNotFoundException(
                "Tipo de sala não encontrado com ID: " + request.tipoSalaId()));

        entity.setNome(request.nome());
        entity.setCargaHoraria(request.cargaHoraria());
        entity.setTipoDisciplina(request.tipoDisciplina());
        entity.setPeriodo(request.periodo());
        entity.setModalidade(request.modalidade());
        entity.setCodDisciplina(request.codDisciplina());
        entity.setCor(request.cor());
        entity.setCurso(curso);
        entity.setTipoSala(tipoSala);

        entity.setUpdatedAt(LocalDateTime.now());
        return DisciplinaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(Long id) {

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Disciplina não encontrada com ID: " + id);
        }

        validarDisciplinaSemVinculos.validar(id);

        repository.deleteById(id);
    }
}

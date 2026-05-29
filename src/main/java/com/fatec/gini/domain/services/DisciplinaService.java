package com.fatec.gini.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.TipoSala;
import com.fatec.gini.domain.services.usecase.read.ValidarDisciplinaSemVinculosUseCase;
import com.fatec.gini.dto.disciplina.DisciplinaRequest;
import com.fatec.gini.dto.disciplina.DisciplinaResponse;
import com.fatec.gini.infrastructure.mappers.DisciplinaMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.TipoSalaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private TipoSalaRepository tipoSalaRepository;

    @Autowired
    private ValidarDisciplinaSemVinculosUseCase validarDisciplinaSemVinculos;

    @Transactional
    public DisciplinaResponse criar(DisciplinaRequest request) {

        Disciplina entity = DisciplinaMapper.toEntity(request);

        Curso curso = cursoRepository.findById(request.curso().id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + request.curso().id()));

        TipoSala tipoSala = tipoSalaRepository.findById(request.tipoSala().id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de sala não encontrado com ID: " + request.tipoSala().id()));

        entity.setCurso(curso);
        entity.setTipoSala(tipoSala);

        Disciplina disciplinaCriada = repository.save(entity);

        return DisciplinaMapper.toResponse(disciplinaCriada);
    }

    @Transactional(readOnly = true)
    public DisciplinaResponse buscarPorId(Long id) {

        Disciplina entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Disciplina não encontrada com ID: " + id));

        return DisciplinaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<DisciplinaResponse> listar(
            String nome,
            Long idCurso,
            Long idTipoSala,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);

        var pageDisciplina = repository.findByFiltros(
                nome,
                idCurso,
                idTipoSala,
                pageRequest);

        return pageDisciplina.map(DisciplinaMapper::toResponse);
    }

    @Transactional
    public DisciplinaResponse atualizar(Long id, DisciplinaRequest request) {

        Disciplina entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Disciplina não encontrada com ID: " + id));

        Curso curso = cursoRepository.findById(request.curso().id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Curso não encontrado com ID: " + request.curso().id()));

        TipoSala tipoSala = tipoSalaRepository.findById(request.tipoSala().id())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de sala não encontrado com ID: " + request.tipoSala().id()));

        entity.setNome(request.nome());
        entity.setCargaHoraria(request.cargaHoraria());
        entity.setTipoDisciplina(request.tipoDisciplina());
        entity.setPeriodo(request.periodo());
        entity.setModalidade(request.modalidade());
        entity.setCodDisciplina(request.codDisciplina());
        entity.setCor(request.cor());
        entity.setCurso(curso);
        entity.setTipoSala(tipoSala);

        Disciplina disciplinaAtualizada = repository.save(entity);

        return DisciplinaMapper.toResponse(disciplinaAtualizada);
    }

    @Transactional
    public void deletar(Long id) {

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Disciplina não encontrada com ID: " + id);
        }

        validarDisciplinaSemVinculos.validar(id);

        repository.deleteById(id);
    }
}
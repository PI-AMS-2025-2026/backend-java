package com.fatec.gini.domain.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.ProfessorDisciplina;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaResponse;
import com.fatec.gini.infrastructure.mappers.ProfessorDisciplinaMapper;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorDisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorRepository;
import com.fatec.gini.web.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessorDisciplinaService {

    private final ProfessorDisciplinaRepository repository;
    private final ProfessorRepository professorRepository;
    private final DisciplinaRepository disciplinaRepository;

    @Transactional
    public ProfessorDisciplinaResponse criar(ProfessorDisciplinaRequest request) {

        if (repository.existsByProfessorIdAndDisciplinaId(
                request.professor().id(), request.disciplina().id())) {
            throw new BusinessException("Relação já existe");
        }

        Professor professor = professorRepository.findById(request.professor().id())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(request.disciplina().id())
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));

        ProfessorDisciplina entity = ProfessorDisciplinaMapper.toEntity(request);
        entity.setProfessor(professor);
        entity.setDisciplina(disciplina);

        return ProfessorDisciplinaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ProfessorDisciplinaResponse buscarPorId(Long id) {
        ProfessorDisciplina entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relação professor-disciplina não encontrada"));

        return ProfessorDisciplinaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProfessorDisciplinaResponse> listar(
            Long professorId,
            Long disciplinaId,
            int pageNum,
            int size) {

        var pageRequest = PageRequest.of(pageNum, size);
        var page = repository.buscarPorFiltros(professorId, disciplinaId, pageRequest);

        return new PageResponse<>(
                page.getContent().stream().map(ProfessorDisciplinaMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalPages());
    }

    @Transactional
    public ProfessorDisciplinaResponse atualizar(Long id, ProfessorDisciplinaRequest request) {

        ProfessorDisciplina entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relação professor-disciplina não encontrada"));

        Professor professor = professorRepository.findById(request.professor().id())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(request.disciplina().id())
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));

        entity.setProfessor(professor);
        entity.setDisciplina(disciplina);

        return ProfessorDisciplinaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(Long id) {
        ProfessorDisciplina entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relação professor-disciplina não encontrada"));

        repository.delete(entity);
    }
}

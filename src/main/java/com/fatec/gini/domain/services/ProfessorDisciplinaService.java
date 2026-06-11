package com.fatec.gini.domain.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.ProfessorDisciplina;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.gini.dto.professorDisciplina.ProfessorDisciplinaResponse;
import com.fatec.gini.infrastructure.mappers.ProfessorDisciplinaMapper;
import com.fatec.gini.infrastructure.repositories.DisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.ProfessorDisciplinaRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;
import com.fatec.gini.web.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessorDisciplinaService {

    private final  ProfessorDisciplinaRepository repository;
    private final  UsuarioRepository usuarioRepository;
    private final  DisciplinaRepository disciplinaRepository;

    @Transactional
    public ProfessorDisciplinaResponse criar(ProfessorDisciplinaRequest request) {

        if (repository.existsByUsuarioIdAndDisciplinaId(
            request.usuario().id(), request.disciplina().id())) {
            throw new BusinessException("Relação já existe");
        }

        Usuario usuario = usuarioRepository.findById(request.usuario().id())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(request.disciplina().id())
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));

        ProfessorDisciplina entity = ProfessorDisciplinaMapper.toEntity(request);
        entity.setUsuario(usuario);
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
    public Page<ProfessorDisciplinaResponse> listar(
            Long usuarioId,
            Long disciplinaId,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);
    return repository.buscarPorFiltros(usuarioId, disciplinaId, pageRequest)
        .map(ProfessorDisciplinaMapper::toResponse);
    }

    @Transactional
    public ProfessorDisciplinaResponse atualizar(Long id, ProfessorDisciplinaRequest request) {

    ProfessorDisciplina entity = repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Relação professor-disciplina não encontrada"));

        Usuario usuario = usuarioRepository.findById(request.usuario().id())
        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(request.disciplina().id())
        .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));

        entity.setUsuario(usuario);
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

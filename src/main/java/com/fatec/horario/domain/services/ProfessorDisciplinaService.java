package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.*;
import com.fatec.horario.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.horario.dto.professorDisciplina.ProfessorDisciplinaResponse;
import com.fatec.horario.infrastructure.mappers.ProfessorDisciplinaMapper;
import com.fatec.horario.infrastructure.repositories.*;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorDisciplinaService {

    @Autowired
    private ProfessorDisciplinaRepository repository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Transactional
    public ProfessorDisciplinaResponse criar(ProfessorDisciplinaRequest request) {

        if (repository.existsByUsuarioIdAndDisciplinaId(
            request.usuarioId(), request.disciplinaId())) {
            throw new RuntimeException("Relação já existe");
        }

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(request.disciplinaId())
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

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(request.disciplinaId())
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

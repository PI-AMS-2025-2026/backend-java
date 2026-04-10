package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.*;
import com.fatec.horario.dto.ProfessorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.horario.infrastructure.repositories.*;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfessorDisciplinaService {

    private final ProfessorDisciplinaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final DisciplinaRepository disciplinaRepository;

    public ProfessorDisciplinaService(
            ProfessorDisciplinaRepository repository,
            UsuarioRepository usuarioRepository,
            DisciplinaRepository disciplinaRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    @Transactional
    public ProfessorDisciplina criar(ProfessorDisciplinaRequest request) {

        if (repository.existsByUsuarioIdUsuarioAndDisciplinaIdDisciplina(
                request.getUsuarioId(), request.getDisciplinaId())) {
            throw new RuntimeException("Relação já existe");
        }

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(request.getDisciplinaId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        ProfessorDisciplina entity = new ProfessorDisciplina(usuario, disciplina);

        return repository.save(entity);
    }

    @Transactional(readOnly = true)
    public ProfessorDisciplina buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<ProfessorDisciplina> listar(Long usuarioId, Long disciplinaId, Pageable pageable) {

        if (usuarioId != null) {
            return repository.findByUsuarioIdUsuario(usuarioId, pageable);
        }

        if (disciplinaId != null) {
            return repository.findByDisciplinaIdDisciplina(disciplinaId, pageable);
        }

        return repository.findAll(pageable);
    }

    @Transactional
    public ProfessorDisciplina atualizar(Long id, ProfessorDisciplinaRequest request) {

        ProfessorDisciplina entity = buscarPorId(id);

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(request.getDisciplinaId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));

        entity.setUsuario(usuario);
        entity.setDisciplina(disciplina);

        return repository.save(entity);
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}

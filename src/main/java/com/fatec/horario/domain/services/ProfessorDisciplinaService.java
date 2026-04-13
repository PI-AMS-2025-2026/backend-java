package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.*;
import com.fatec.horario.dto.professorDisciplina.ProfessorDisciplinaRequest;
import com.fatec.horario.infrastructure.repositories.*;

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
    public ProfessorDisciplina criar(ProfessorDisciplinaRequest request) {

        //TODO: Estudar melhor a validação de unico
        if (repository.existsByUsuarioIdUsuarioAndDisciplinaIdDisciplina(
            request.usuarioId(), request.disciplinaId())) {
            throw new RuntimeException("Relação já existe");
        }

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(request.disciplinaId())
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
    public Page<ProfessorDisciplina> listar(
            Long usuarioId,
            Long disciplinaId,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);
        return repository.buscarPorFiltros(usuarioId, disciplinaId, pageRequest);
    }

    @Transactional
    public ProfessorDisciplina atualizar(Long id, ProfessorDisciplinaRequest request) {

        ProfessorDisciplina entity = buscarPorId(id);

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(request.disciplinaId())
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

package com.fatec.horario.domain.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.domain.entities.Course;
import com.fatec.horario.dto.Turma.TurmaRequest;
import com.fatec.horario.dto.Turma.TurmaResponse;
import com.fatec.horario.infrastructure.mappers.TurmaMapper;
import com.fatec.horario.infrastructure.repositories.TurmaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository repository;

    @Transactional(readOnly = true)
    public List<TurmaResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(TurmaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TurmaResponse getById(long id) {
        return repository.findById(id)
                .map(TurmaMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada"));
    }

    @Transactional
    public void delete(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Turma não encontrada");
        }
    }

    @Transactional
    public TurmaResponse create(TurmaRequest request) {

        Turma turma = TurmaMapper.toEntity(request);

        turma = repository.save(turma);

        return TurmaMapper.toResponse(turma);
    }

    @Transactional
    public TurmaResponse update(TurmaRequest request, long id) {

        Turma turma = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada"));

        // Atualizando TODOS os campos
        turma.setCodigo(request.codigo());
        turma.setPeriodo(request.periodo());
        turma.setAno(request.ano());
        turma.setNumeroAlunos(request.numeroAlunos());

        // Atualiza o curso (FK)
        Course curso = new Course();
        curso.setId(request.idCurso());
        turma.setCurso(curso);

        turma = repository.save(turma);

        return TurmaMapper.toResponse(turma);
    }
}

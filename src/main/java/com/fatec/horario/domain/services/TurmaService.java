package com.fatec.horario.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.dto.Turma.TurmaRequest;
import com.fatec.horario.dto.Turma.TurmaResponse;
import com.fatec.horario.infrastructure.mappers.TurmaMapper;
import com.fatec.horario.infrastructure.repositories.TurmaRepository;
import com.fatec.horario.infrastructure.repositories.CursoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    // PAGINAÇÃO + FILTROS
    @Transactional(readOnly = true)
    public Page<TurmaResponse> getAll(Long idCurso, Integer ano, Integer periodo, Pageable pageable) {

        Page<Turma> page = repository.findByFilters(idCurso, ano, periodo, pageable);

        return page.map(TurmaMapper::toResponse);
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

        //  Busca o curso corretamente no banco
        Curso curso = cursoRepository.findById(request.idCurso())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        turma.setCurso(curso);

        turma = repository.save(turma);

        return TurmaMapper.toResponse(turma);
    }

    @Transactional
    public TurmaResponse update(TurmaRequest request, long id) {

        Turma turma = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada"));

        turma.setCodigo(request.codigo());
        turma.setPeriodo(request.periodo());
        turma.setAno(request.ano());
        turma.setNumeroAlunos(request.numeroAlunos());

        // Atualiza curso corretamente
        Curso curso = cursoRepository.findById(request.idCurso())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        turma.setCurso(curso);

        turma = repository.save(turma);

        return TurmaMapper.toResponse(turma);
    }
}

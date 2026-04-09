package com.fatec.horario.domain.services;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.dto.Turma.TurmaRequest;
import com.fatec.horario.dto.Turma.TurmaResponse;
import com.fatec.horario.infrastructure.mappers.TurmaMapper;
import com.fatec.horario.infrastructure.repositories.CursoRepository;
import com.fatec.horario.infrastructure.repositories.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @Transactional
    public TurmaResponse criar(TurmaRequest request) {
        Turma entity = TurmaMapper.toEntity(request);
        
        Curso curso = cursoRepository.findById(request.idCurso())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + request.idCurso()));
        entity.setCurso(curso);

        return TurmaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public TurmaResponse buscarPorId(long id) {
        Turma entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));
        return TurmaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<TurmaResponse> listar(
        Long idCurso, 
        Integer ano, 
        Integer periodo, 
        String codigo, 
        int page, 
        int size) {       
        var pageRequest = PageRequest.of(page, size);
        return repository.buscarPorFiltros(idCurso, ano, periodo, codigo, pageRequest)
                .map(TurmaMapper::toResponse);
    }

    @Transactional
    public TurmaResponse atualizar(long id, TurmaRequest request) {
        Turma entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com ID: " + id));

        Curso curso = cursoRepository.findById(request.idCurso())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + request.idCurso()));

        entity.setCodigo(request.codigo());
        entity.setAno(request.ano());
        entity.setPeriodo(request.periodo());
        entity.setNumeroAlunos(request.numeroAlunos());
        entity.setCurso(curso);

        return TurmaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Turma não encontrada com ID: " + id);
        }
        repository.deleteById(id);
    }
}
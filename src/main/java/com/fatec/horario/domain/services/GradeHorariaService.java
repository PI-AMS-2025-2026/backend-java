package com.fatec.horario.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.PeriodoLetivo;
import com.fatec.horario.dto.GradeHoraria.GradeHorariaRequest;
import com.fatec.horario.dto.GradeHoraria.GradeHorariaResponse;
import com.fatec.horario.infrastructure.mappers.GradeHorariaMapper;
import com.fatec.horario.infrastructure.repositories.CursoRepository;
import com.fatec.horario.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.horario.infrastructure.repositories.PeriodoLetivoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GradeHorariaService {

    @Autowired
    private GradeHorariaRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private PeriodoLetivoRepository periodoRepository;

    @Transactional
    public GradeHorariaResponse criar(GradeHorariaRequest request) {

        GradeHoraria entity = GradeHorariaMapper.toEntity(request);

        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + request.cursoId()));

        PeriodoLetivo periodo = periodoRepository.findById(request.periodoLetivoId())
                .orElseThrow(() -> new EntityNotFoundException("Período letivo não encontrado com ID: " + request.periodoLetivoId()));

        entity.setCurso(curso);
        entity.setPeriodoLetivo(periodo);

        return GradeHorariaMapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public GradeHorariaResponse buscarPorId(long id) {

        GradeHoraria entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade horária não encontrada com ID: " + id));

        return GradeHorariaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<GradeHorariaResponse> listar(
            Long curso,
            Long periodoLetivo,
            String status,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);

        var pageGrade = repository.buscarComFiltros(
                curso,
                periodoLetivo,
                status,
                pageRequest);

        return pageGrade.map(GradeHorariaMapper::toResponse);
    }

    @Transactional
    public GradeHorariaResponse atualizar(long id, GradeHorariaRequest request) {

        GradeHoraria entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade horária não encontrada com ID: " + id));

        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com ID: " + request.cursoId()));

        PeriodoLetivo periodo = periodoRepository.findById(request.periodoLetivoId())
                .orElseThrow(() -> new EntityNotFoundException("Período letivo não encontrado com ID: " + request.periodoLetivoId()));

        entity.setVersao(request.versao());
        entity.setStatus(request.status());
        entity.setCurso(curso);
        entity.setPeriodoLetivo(periodo);

        return GradeHorariaMapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void deletar(long id) {

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Grade horária não encontrada com ID: " + id);
        }

        repository.deleteById(id);
    }
}
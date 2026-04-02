package com.fatec.horario.domain.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.horario.domain.entities.Curso;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.PeriodoLetivo;
import com.fatec.horario.dto.GradeHoraria.GradeHorariaRequest;
import com.fatec.horario.dto.GradeHoraria.GradeHorariaResponse;
import com.fatec.horario.infrastructure.repositories.CursoRepository;
import com.fatec.horario.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.horario.infrastructure.repositories.PeriodoLetivoRepository;
import com.fatec.horario.infrastructure.mappers.GradeHorariaMapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GradeHorariaService {

    private final GradeHorariaRepository repository;
    private final CursoRepository cursoRepository;
    private final PeriodoLetivoRepository periodoRepository;

    public GradeHorariaService(
            GradeHorariaRepository repository,
            CursoRepository cursoRepository,
            PeriodoLetivoRepository periodoRepository) {

        this.repository = repository;
        this.cursoRepository = cursoRepository;
        this.periodoRepository = periodoRepository;
    }

    @Transactional
    public GradeHorariaResponse criar(GradeHorariaRequest request) {

        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        PeriodoLetivo periodo = periodoRepository.findById(request.periodoLetivoId())
                .orElseThrow(() -> new EntityNotFoundException("Periodo letivo não encontrado"));

        GradeHoraria grade = GradeHorariaMapper.toEntity(request, curso, periodo);

        repository.save(grade);

        return GradeHorariaMapper.toResponse(grade);
    }

    @Transactional(readOnly = true)
    public GradeHorariaResponse buscarPorId(Long id) {

        GradeHoraria grade = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade horaria não encontrada"));

        return GradeHorariaMapper.toResponse(grade);
    }

    @Transactional(readOnly = true)
    public Page<GradeHorariaResponse> listar(
            Long curso,
            Long periodoLetivo,
            String status,
            Pageable pageable) {

        Page<GradeHoraria> page = repository.buscarComFiltros(
                curso,
                periodoLetivo,
                status,
                pageable
        );

        return page.map(GradeHorariaMapper::toResponse);
    }

    @Transactional
    public GradeHorariaResponse atualizar(Long id, GradeHorariaRequest request) {

        GradeHoraria grade = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade horaria não encontrada"));

        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        PeriodoLetivo periodo = periodoRepository.findById(request.periodoLetivoId())
                .orElseThrow(() -> new EntityNotFoundException("Periodo letivo não encontrado"));

        grade.setVersao(request.versao());
        grade.setStatus(request.status());
        grade.setCurso(curso);
        grade.setPeriodoLetivo(periodo);

        repository.save(grade);

        return GradeHorariaMapper.toResponse(grade);
    }

    @Transactional
    public void deletar(Long id) {

        GradeHoraria grade = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grade horaria não encontrada"));

        repository.delete(grade);
    }
}
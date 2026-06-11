package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.GradeHoraria;
import com.fatec.gini.domain.entities.PeriodoLetivo;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarPeriodoLetivoAtivoUseCase;
import com.fatec.gini.domain.services.usecase.write.ValidarGradeHorariaValidaUseCase;
import com.fatec.gini.dto.gradeHoraria.GradeHorariaRequest;
import com.fatec.gini.dto.gradeHoraria.GradeHorariaResponse;
import com.fatec.gini.infrastructure.mappers.GradeHorariaMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.GradeHorariaRepository;
import com.fatec.gini.infrastructure.repositories.PeriodoLetivoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GradeHorariaService {

    @Autowired
    private GradeHorariaRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private PeriodoLetivoRepository periodoRepository;

    @Autowired
    private ValidarGradeHorariaValidaUseCase validarGradeHorariaValidaUseCase;

    @Autowired
    private ValidarPeriodoLetivoAtivoUseCase validarPeriodoLetivoAtivoUseCase;

    @Transactional
    public GradeHorariaResponse criar(GradeHorariaRequest request) {

        GradeHoraria entity = GradeHorariaMapper.toEntity(request);

        Curso curso = cursoRepository.findById(request.curso().id())
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Curso não encontrado com ID: "
                        + request.curso().id()
                    )
                );

        PeriodoLetivo periodo = periodoRepository.findById(
                request.periodoLetivo().id()
        ).orElseThrow(() ->
            new EntityNotFoundException(
                "Período letivo não encontrado com ID: "
                + request.periodoLetivo().id()
            )
        );

        entity.setCurso(curso);
        entity.setPeriodoLetivo(periodo);

        entity.setDataCriacao(LocalDateTime.now());
        entity.setStatus(request.status());
        LocalDateTime agora = LocalDateTime.now();

        entity.setCreatedAt(agora);
        entity.setUpdatedAt(agora);

        validarGradeHorariaValidaUseCase
                .validarGradeAtivaDuplicada(entity);

        validarPeriodoLetivoAtivoUseCase.executar(entity);

        return GradeHorariaMapper.toResponse(
                repository.save(entity)
        );
    }

    @Transactional(readOnly = true)
    public GradeHorariaResponse buscarPorId(Long id) {

        GradeHoraria entity = repository.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Grade horária não encontrada com ID: "
                        + id
                    )
                );

        return GradeHorariaMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<GradeHorariaResponse> listar(
            Long idCurso,
            Long idPeriodoLetivo,
            Status status,
            int page,
            int size) {

        var pageRequest = PageRequest.of(page, size);

        var pageGrade = repository.buscarComFiltros(
                idCurso,
                idPeriodoLetivo,
                status,
                pageRequest
        );

        return pageGrade.map(GradeHorariaMapper::toResponse);
    }

    @Transactional
    public GradeHorariaResponse atualizar(
            Long id,
            GradeHorariaRequest request
    ) {

        GradeHoraria entity = repository.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Grade horária não encontrada com ID: "
                        + id
                    )
                );

        Curso curso = cursoRepository.findById(request.curso().id())
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Curso não encontrado com ID: "
                        + request.curso().id()
                    )
                );

        PeriodoLetivo periodo = periodoRepository.findById(
                request.periodoLetivo().id()
        ).orElseThrow(() ->
            new EntityNotFoundException(
                "Período letivo não encontrado com ID: "
                + request.periodoLetivo().id()
            )
        );

        entity.setVersao(request.versao());
        entity.setStatus(request.status());
        entity.setCurso(curso);
        entity.setPeriodoLetivo(periodo);
        entity.setUpdatedAt(LocalDateTime.now());

        validarGradeHorariaValidaUseCase
                .validarGradeAtivaDuplicada(entity);

        validarPeriodoLetivoAtivoUseCase.executar(entity);

        return GradeHorariaMapper.toResponse(
                repository.save(entity)
        );
    }

    @Transactional
    public void inativar(Long id) {

        GradeHoraria entity = repository.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                        "Grade horária não encontrada com ID: "
                        + id
                    )
                );

        entity.setStatus(Status.INATIVO);
        entity.setUpdatedAt(LocalDateTime.now());

        repository.save(entity);
    }
}
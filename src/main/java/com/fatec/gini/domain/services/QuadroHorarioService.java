package com.fatec.gini.domain.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.PeriodoLetivo;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarPeriodoLetivoAtivoUseCase;
import com.fatec.gini.domain.services.usecase.write.ValidarQuadroHorarioValidoUseCase;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioRequest;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioResponse;
import com.fatec.gini.infrastructure.mappers.QuadroHorarioMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.infrastructure.repositories.PeriodoLetivoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuadroHorarioService {

 private final QuadroHorarioRepository repository;

 private final CursoRepository cursoRepository;

 private final PeriodoLetivoRepository periodoRepository;

 private final ValidarQuadroHorarioValidoUseCase validarQuadroHorarioValidoUseCase;

 private final ValidarPeriodoLetivoAtivoUseCase validarPeriodoLetivoAtivoUseCase;

        @Transactional
        public QuadroHorarioResponse criar(QuadroHorarioRequest request) {
                QuadroHorario entity = QuadroHorarioMapper.toEntity(request);

                Curso curso = cursoRepository.findById(request.curso().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Curso não encontrado com ID: " + request.curso().id()));

                PeriodoLetivo periodo = periodoRepository.findById(request.periodoLetivo().id())
                                .orElseThrow(() -> new EntityNotFoundException("Período letivo não encontrado com ID: "
                                                + request.periodoLetivo().id()));

                entity.setCurso(curso);
                entity.setPeriodoLetivo(periodo);
                entity.setDataCriacao(LocalDateTime.now());
                entity.setStatus(request.status());
                validarQuadroHorarioValidoUseCase.validarQuadroAtivoDuplicado(entity);
                validarPeriodoLetivoAtivoUseCase.executar(entity);

                return QuadroHorarioMapper.toResponse(repository.save(entity));
        }

        @Transactional(readOnly = true)
        public QuadroHorarioResponse buscarPorId(Long id) {
                QuadroHorario entity = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Quadro horário não encontrada com ID: " + id));
                return QuadroHorarioMapper.toResponse(entity);
        }

        @Transactional(readOnly = true)
        public Page<QuadroHorarioResponse> listar(
                        Long idCurso,
                        Long idPeriodoLetivo,
                        Status status,
                        int page,
                        int size) {

                var pageRequest = PageRequest.of(page, size);

                var pageQuadro = repository.buscarComFiltros(
                                idCurso,
                                idPeriodoLetivo,
                                status,
                                pageRequest);

                return pageQuadro.map(QuadroHorarioMapper::toResponse);
        }

        @Transactional
        public QuadroHorarioResponse atualizar(Long id, QuadroHorarioRequest request) {
                QuadroHorario entity = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Quadro horário não encontrada com ID: " + id));

                Curso curso = cursoRepository.findById(request.curso().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Curso não encontrado com ID: " + request.curso().id()));

                PeriodoLetivo periodo = periodoRepository.findById(request.periodoLetivo().id())
                                .orElseThrow(() -> new EntityNotFoundException("Período letivo não encontrado com ID: "
                                                + request.periodoLetivo().id()));

                entity.setVersao(request.versao());
                entity.setStatus(request.status());
                entity.setCurso(curso);
                entity.setPeriodoLetivo(periodo);
                validarQuadroHorarioValidoUseCase.validarQuadroAtivoDuplicado(entity);
                validarPeriodoLetivoAtivoUseCase.executar(entity);
                return QuadroHorarioMapper.toResponse(repository.save(entity));
        }

        @Transactional
        public void inativar(Long id) {
                QuadroHorario entity = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Quadro horário não encontrada com ID: " + id));

                entity.setStatus(Status.INATIVO);
                repository.save(entity);
        }
}
package com.fatec.gini.domain.services;

import java.time.LocalDateTime;
import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCursoAtivoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarPeriodoAtividadeQuadroAtivoUseCase;
import com.fatec.gini.domain.services.usecase.write.CopiarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.write.MotorQuadroHorario;
import com.fatec.gini.domain.services.usecase.write.ValidarQuadroHorarioValidoUseCase;
import com.fatec.gini.dto.grade.GradeCursoResponse;
import com.fatec.gini.dto.paginacao.PageResponse;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioRequest;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioResponse;
import com.fatec.gini.infrastructure.mappers.QuadroHorarioMapper;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;
import com.fatec.gini.web.exception.BusinessException;
import com.fatec.gini.infrastructure.repositories.PeriodoAtividadeQuadroRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuadroHorarioService {

        private final QuadroHorarioRepository repository;

        private final CursoRepository cursoRepository;

        private final TurmaRepository turmaRepository;

        private final PeriodoAtividadeQuadroRepository periodoRepository;

        private final ValidarQuadroHorarioValidoUseCase validarQuadroHorarioValidoUseCase;

        private final ValidarPeriodoAtividadeQuadroAtivoUseCase validarPeriodoAtividadeQuadroAtivoUseCase;

        private final CopiarQuadroHorarioUseCase copiarQuadroHorarioUseCase;

        private final ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

        private final ValidarCursoAtivoUseCase validarCursoAtivoUseCase;

        private final MotorQuadroHorario motorQuadroHorario;

        public GradeCursoResponse obterGradeCurso(Long cursoId) {
                return motorQuadroHorario.contruir(cursoId);
        }

        @Transactional
        public QuadroHorarioResponse copiar(Long id, QuadroHorarioRequest request) {

                QuadroHorario origem = repository.findById(id).orElseThrow(
                                () -> new EntityNotFoundException("Quadro horário não encontrada com ID: " + id));
                validarAutorizacaoCurso.validarCurso(origem.getCurso());

                if (origem.getTurma() == null || request.turma() == null
                                || !origem.getTurma().getId().equals(request.turma().id())
                                || !origem.getPeriodoAtividadeQuadro().getId().equals(request.periodoAtividadeQuadro().id())) {
                        throw new BusinessException("A cópia deve pertencer à mesma turma e período do quadro de origem.");
                }

                QuadroHorario dadosNovaGrade = QuadroHorarioMapper.toEntity(request);

                dadosNovaGrade.setPeriodoAtividadeQuadro(
                                periodoRepository.findById(
                                                request.periodoAtividadeQuadro().id())
                                                .orElseThrow(() -> new EntityNotFoundException(
                                                                "Período Atividade Quadro não encontrado com ID: "
                                                                                + request.periodoAtividadeQuadro()
                                                                                                .id())));

                QuadroHorario copia = copiarQuadroHorarioUseCase.executar(
                                id,
                                dadosNovaGrade);

                return QuadroHorarioMapper.toResponse(copia);
        }

        @Transactional
        public QuadroHorarioResponse criar(QuadroHorarioRequest request) {

                QuadroHorario entity = QuadroHorarioMapper.toEntity(request);

                /*
                 * Busca o curso informado.
                 */
                Curso curso = cursoRepository.findById(
                                request.curso().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Curso não encontrado com ID: "
                                                                + request.curso().id()));
                validarAutorizacaoCurso.validarCurso(curso);

                /*
                 * O curso precisa estar ativo para criação
                 * de um quadro horário.
                 */
                validarCursoAtivoUseCase.validar(curso);

                /*
                 * Busca o período informado.
                 */
                PeriodoAtividadeQuadro periodo = periodoRepository.findById(
                                request.periodoAtividadeQuadro().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Período Atividade Quadro não encontrado com ID:  "
                                                                + request.periodoAtividadeQuadro().id()));

                var turma = turmaRepository.findById(request.turma().id())
                                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada."));
                validarAutorizacaoCurso.validarCursosIguais(curso, turma.getCurso());
                entity.setTurma(turma);
                entity.setVersao(repository.findTopByTurmaIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(turma.getId(), periodo.getId())
                                .map(quadro -> quadro.getVersao() + 1).orElse(1));
                entity.setCurso(curso);
                entity.setPeriodoAtividadeQuadro(periodo);

                entity.setDataCriacao(
                                LocalDateTime.now());

                /*
                 * Todo novo quadro horário deve iniciar como ATIVO,
                 * independentemente do status enviado na requisição.
                 */
                entity.setStatus(Status.ATIVO);

                /*
                 * Verifica se já existe outro quadro ativo
                 * para o mesmo curso e período.
                 */
                validarQuadroHorarioValidoUseCase
                                .validarQuadroAtivoDuplicado(entity);

                /*
                 * Valida se o período está ativo.
                 */
                validarPeriodoAtividadeQuadroAtivoUseCase
                                .executar(entity);

                LocalDateTime agora = LocalDateTime.now();

                entity.setCreatedAt(agora);
                entity.setUpdatedAt(agora);

                return QuadroHorarioMapper.toResponse(
                                repository.save(entity));
        }

        @Transactional(readOnly = true)
        public QuadroHorarioResponse buscarPorId(
                        Long id) {

                QuadroHorario entity = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Quadro de horários não encontrado com ID: " + id));
                validarAutorizacaoCurso.validarCurso(entity.getCurso());
                return QuadroHorarioMapper.toResponse(entity);
        }

        @Transactional(readOnly = true)
        public PageResponse<QuadroHorarioResponse> listar(
                        Long idCurso,
                        Long idTurma,
                        Long idPeriodoAtividadeQuadro,
                        Status status,
                        Integer versao,
                        LocalDate dataCriacao,
                        String busca,
                        int pageNum,
                        int size) {

                var pageRequest = PageRequest.of(
                                pageNum,
                                size);

                var page = repository.buscarComFiltros(
                                validarAutorizacaoCurso.cursoParaFiltro(idCurso),
                                idTurma,
                                idPeriodoAtividadeQuadro,
                                status,
                                versao,
                                dataCriacao == null ? null : dataCriacao.atStartOfDay(),
                                dataCriacao == null ? null : dataCriacao.plusDays(1).atStartOfDay(),
                                busca,
                                pageRequest);

                return new PageResponse<>(
                                page.getContent()
                                                .stream()
                                                .map(QuadroHorarioMapper::toResponse)
                                                .toList(),
                                page.getNumber(),
                                page.getSize(),
                                page.getTotalElements(),
                                page.getTotalPages());
        }

        @Transactional
        public QuadroHorarioResponse atualizar(
                        Long id,
                        QuadroHorarioRequest request) {

                QuadroHorario entity = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Quadro de horários não encontrado com ID: " + id));
                validarAutorizacaoCurso.validarCurso(entity.getCurso());

                /*
                 * Busca o curso informado na atualização.
                 */
                Curso curso = cursoRepository.findById(
                                request.curso().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Curso não encontrado com ID: "
                                                                + request.curso().id()));
                validarAutorizacaoCurso.validarCurso(curso);

                /*
                 * O curso precisa estar ativo para alteração
                 * do quadro horário.
                 */
                validarCursoAtivoUseCase.validar(curso);

                /*
                 * Busca o período informado.
                 */
                PeriodoAtividadeQuadro periodo = periodoRepository.findById(
                                request.periodoAtividadeQuadro().id())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Período Atividade Quadro não encontrado com ID:  "
                                                                + request.periodoAtividadeQuadro().id()));

                if (entity.getTurma() == null || request.turma() == null
                                || !entity.getTurma().getId().equals(request.turma().id())
                                || !entity.getCurso().getId().equals(curso.getId())
                                || !entity.getPeriodoAtividadeQuadro().getId().equals(periodo.getId())) {
                        throw new BusinessException("Não é permitido alterar turma, curso ou período do quadro.");
                }
                if (!entity.getVersao().equals(request.versao())) {
                        throw new BusinessException("Não é permitido alterar a versão do quadro.");
                }

                entity.setStatus(
                                request.status());

                entity.setCurso(curso);

                entity.setPeriodoAtividadeQuadro(
                                periodo);

                /*
                 * A validação utiliza o ID do próprio quadro para
                 * não considerá-lo como duplicado.
                 */
                validarQuadroHorarioValidoUseCase
                                .validarQuadroAtivoDuplicado(entity);

                /*
                 * Valida se o período está ativo.
                 */
                validarPeriodoAtividadeQuadroAtivoUseCase
                                .executar(entity);

                entity.setUpdatedAt(
                                LocalDateTime.now());

                return QuadroHorarioMapper.toResponse(
                                repository.save(entity));
        }

        @Transactional
        public void inativar(
                        Long id) {

                QuadroHorario entity = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Quadro de horários não encontrado com ID: " + id));
                validarAutorizacaoCurso.validarCurso(entity.getCurso());

                entity.setStatus(Status.INATIVO);
                entity.setUpdatedAt(
                                LocalDateTime.now());

                repository.save(entity);
        }
}

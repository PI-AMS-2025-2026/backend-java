package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCursoAtivoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarPeriodoAtividadeQuadroAtivoUseCase;
import com.fatec.gini.domain.services.usecase.write.CopiarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.write.MotorQuadroHorario;
import com.fatec.gini.domain.services.usecase.write.ValidarQuadroHorarioValidoUseCase;
import com.fatec.gini.dto.id.LongDTO;
import com.fatec.gini.dto.quadroHorario.QuadroHorarioRequest;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.PeriodoAtividadeQuadroRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class QuadroHorarioServiceTest {

    @Mock private QuadroHorarioRepository repository;
    @Mock private CursoRepository cursoRepository;
    @Mock private TurmaRepository turmaRepository;
    @Mock private PeriodoAtividadeQuadroRepository periodoRepository;
    @Mock private ValidarQuadroHorarioValidoUseCase validarQuadroHorarioValidoUseCase;
    @Mock private ValidarPeriodoAtividadeQuadroAtivoUseCase validarPeriodoAtividadeQuadroAtivoUseCase;
    @Mock private CopiarQuadroHorarioUseCase copiarQuadroHorarioUseCase;
    @Mock private ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;
    @Mock private ValidarCursoAtivoUseCase validarCursoAtivoUseCase;
    @Mock private MotorQuadroHorario motorQuadroHorario;

    @InjectMocks
    private QuadroHorarioService service;

    @Test
    void permiteCopiarParaOutroPeriodoDaMesmaTurmaEValidaDestinoAtivo() {
        QuadroHorario origem = quadro(10L, turma(5L), periodo(20L));
        PeriodoAtividadeQuadro periodoDestino = periodo(30L);
        QuadroHorario copia = quadro(11L, turma(5L), periodoDestino);
        QuadroHorarioRequest request = request(5L, 30L);
        when(repository.findById(10L)).thenReturn(Optional.of(origem));
        when(periodoRepository.findById(30L)).thenReturn(Optional.of(periodoDestino));
        when(copiarQuadroHorarioUseCase.executar(eq(10L), any(QuadroHorario.class))).thenReturn(copia);

        service.copiar(10L, request);

        ArgumentCaptor<QuadroHorario> destinoCaptor = ArgumentCaptor.forClass(QuadroHorario.class);
        verify(validarPeriodoAtividadeQuadroAtivoUseCase).executar(destinoCaptor.capture());
        assertThat(destinoCaptor.getValue().getPeriodoAtividadeQuadro()).isSameAs(periodoDestino);
        verify(copiarQuadroHorarioUseCase).executar(eq(10L), any(QuadroHorario.class));
    }

    @Test
    void rejeitaCopiaParaTurmaDiferenteDaOrigem() {
        QuadroHorario origem = quadro(10L, turma(5L), periodo(20L));
        when(repository.findById(10L)).thenReturn(Optional.of(origem));

        assertThatThrownBy(() -> service.copiar(10L, request(6L, 30L)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A cópia deve pertencer à mesma turma do quadro de origem.");

        verify(periodoRepository, never()).findById(30L);
        verify(copiarQuadroHorarioUseCase, never()).executar(eq(10L), any(QuadroHorario.class));
    }

    @Test
    void interrompeCopiaQuandoPeriodoDeDestinoEstaInativo() {
        QuadroHorario origem = quadro(10L, turma(5L), periodo(20L));
        PeriodoAtividadeQuadro periodoDestino = periodo(30L);
        periodoDestino.setStatus(Status.INATIVO);
        when(repository.findById(10L)).thenReturn(Optional.of(origem));
        when(periodoRepository.findById(30L)).thenReturn(Optional.of(periodoDestino));
        doThrow(new BusinessException(
                "Não é permitido criar ou alterar quadro horário para um período atividade quadro inativo."))
                .when(validarPeriodoAtividadeQuadroAtivoUseCase).executar(any(QuadroHorario.class));

        assertThatThrownBy(() -> service.copiar(10L, request(5L, 30L)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é permitido criar ou alterar quadro horário para um período atividade quadro inativo.");
        verify(copiarQuadroHorarioUseCase, never()).executar(eq(10L), any(QuadroHorario.class));
    }

    private QuadroHorarioRequest request(long turmaId, long periodoId) {
        return new QuadroHorarioRequest(1, Status.ATIVO, new LongDTO(7L),
                new LongDTO(turmaId), new LongDTO(periodoId));
    }

    private QuadroHorario quadro(long id, Turma turma, PeriodoAtividadeQuadro periodo) {
        Curso curso = turma.getCurso();
        QuadroHorario quadro = new QuadroHorario();
        quadro.setId(id);
        quadro.setVersao(1);
        quadro.setStatus(Status.ATIVO);
        quadro.setCurso(curso);
        quadro.setTurma(turma);
        quadro.setPeriodoAtividadeQuadro(periodo);
        return quadro;
    }

    private Turma turma(long id) {
        Curso curso = new Curso();
        curso.setId(7L);
        Turma turma = new Turma();
        turma.setId(id);
        turma.setCurso(curso);
        return turma;
    }

    private PeriodoAtividadeQuadro periodo(long id) {
        PeriodoAtividadeQuadro periodo = new PeriodoAtividadeQuadro();
        periodo.setId(id);
        periodo.setAno(2027);
        periodo.setPeriodo(1);
        periodo.setStatus(Status.ATIVO);
        return periodo;
    }
}

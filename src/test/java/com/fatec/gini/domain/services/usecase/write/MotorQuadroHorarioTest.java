package com.fatec.gini.domain.services.usecase.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;
import com.fatec.gini.dto.grade.GradeCursoResponse;
import com.fatec.gini.dto.grade.GradeTurmaResponse;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.infrastructure.repositories.TurmaRepository;

import jakarta.persistence.EntityNotFoundException;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class MotorQuadroHorarioTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private QuadroHorarioRepository quadroHorarioRepository;

    @Mock
    private TurmaRepository turmaRepository;

    @Mock
    private AlocacaoRepository alocacaoRepository;

    @Mock
    private ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

    private MotorQuadroHorario motor;
    private Curso curso;
    private QuadroHorario quadro;
    private Turma turma;
    private Alocacao alocacao;

    @BeforeEach
    void setUp() {
        motor = new MotorQuadroHorario(
                cursoRepository,
                quadroHorarioRepository,
                turmaRepository,
                alocacaoRepository,
                validarAutorizacaoCurso);

        curso = new Curso("Análise e Desenvolvimento de Sistemas", "Semestral", Status.ATIVO, 6);
        curso.setId(1L);

        quadro = new QuadroHorario(1, Status.ATIVO);
        quadro.setId(10L);

        turma = new Turma("A", 1, 1, 30);
        turma.setId(20L);
        turma.setCurso(curso);

        BlocoHorario bloco = new BlocoHorario(LocalTime.of(19, 0), LocalTime.of(20, 0), 60);
        bloco.setId(30L);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(40L);
        disciplina.setNome("Projeto Integrador");

        Sala sala = new Sala("LAB 01", 30);
        sala.setId(50L);

        alocacao = new Alocacao(turma, disciplina, sala, null, DiaSemana.SEGUNDA, bloco, quadro);
        alocacao.setId(60L);
    }

    @Test
    void deveConstruirGradeCompletaDoCurso() {
        configurarCursoComAlocacoes();

        GradeCursoResponse resultado = motor.construir(1L);

        assertThat(resultado.curso().id()).isEqualTo(1L);
        assertThat(resultado.quadroHorario().id()).isEqualTo(10L);
        assertThat(resultado.blocos()).hasSize(1);
        assertThat(resultado.turmas()).hasSize(1);
        assertThat(resultado.turmas().get(0).celulas()).hasSize(1);
        assertThat(resultado.turmas().get(0).celulas().get(0).disciplina().nome())
                .isEqualTo("Projeto Integrador");
    }

    @Test
    void deveConstruirGradeDeUmaTurmaDoCurso() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(quadroHorarioRepository.findByCursoIdAndStatus(1L, Status.ATIVO)).thenReturn(List.of(quadro));
        when(turmaRepository.findByIdAndCursoId(20L, 1L)).thenReturn(Optional.of(turma));
        when(alocacaoRepository.buscarAlocacoesGradeTurma(1L, 20L, 10L)).thenReturn(List.of(alocacao));

        GradeTurmaResponse resultado = motor.construir(1L, 20L);

        assertThat(resultado.curso().id()).isEqualTo(1L);
        assertThat(resultado.turma().id()).isEqualTo(20L);
        assertThat(resultado.horarios()).hasSize(1);
        assertThat(resultado.horarios().get(0).disciplina().nome()).isEqualTo("Projeto Integrador");
    }

    @Test
    void deveRejeitarTurmaQueNaoPertenceAoCurso() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(turmaRepository.findByIdAndCursoId(20L, 1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> motor.construir(1L, 20L));
        verify(quadroHorarioRepository, never()).findByCursoIdAndStatus(any(), any());
    }

    @Test
    void deveRejeitarCursoSemQuadroAtivo() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(quadroHorarioRepository.findByCursoIdAndStatus(1L, Status.ATIVO)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> motor.construir(1L));
    }

    @Test
    void deveRejeitarMaisDeUmQuadroAtivo() {
        QuadroHorario segundoQuadro = new QuadroHorario(2, Status.ATIVO);
        segundoQuadro.setId(11L);
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(quadroHorarioRepository.findByCursoIdAndStatus(1L, Status.ATIVO))
                .thenReturn(List.of(quadro, segundoQuadro));

        assertThrows(BusinessException.class, () -> motor.construir(1L));
    }

    private void configurarCursoComAlocacoes() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(quadroHorarioRepository.findByCursoIdAndStatus(1L, Status.ATIVO)).thenReturn(List.of(quadro));
        when(alocacaoRepository.buscarAlocacoesGradeCurso(1L, 10L)).thenReturn(List.of(alocacao));
        when(turmaRepository.findByCursoIdOrderByAnoAscPeriodoAscCodigoAsc(1L)).thenReturn(List.of(turma));
    }
}
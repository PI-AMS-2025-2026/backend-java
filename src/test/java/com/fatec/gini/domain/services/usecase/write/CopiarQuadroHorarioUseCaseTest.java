package com.fatec.gini.domain.services.usecase.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarCopiarQuadroHorarioUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarCursoAtivoUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class CopiarQuadroHorarioUseCaseTest {

    @Mock
    private QuadroHorarioRepository quadroRepository;

    @Mock
    private AlocacaoRepository alocacaoRepository;

    @Mock
    private ValidarCopiarQuadroHorarioUseCase validarCopia;

    @Mock
    private ValidarQuadroHorarioValidoUseCase validarQuadroValido;

    @Mock
    private ValidarCursoAtivoUseCase validarCursoAtivo;

    @InjectMocks
    private CopiarQuadroHorarioUseCase useCase;

    @Test
    void calculaProximaVersaoNoPeriodoDeDestino() {
        Curso curso = new Curso();
        curso.setId(7L);
        Turma turma = new Turma();
        turma.setId(8L);
        turma.setCurso(curso);
        PeriodoAtividadeQuadro origemPeriodo = periodo(10L);
        PeriodoAtividadeQuadro destinoPeriodo = periodo(20L);
        QuadroHorario origem = quadro(1L, 3, turma, curso, origemPeriodo);
        QuadroHorario ultimaVersaoDestino = quadro(2L, 4, turma, curso, destinoPeriodo);
        QuadroHorario dadosDestino = new QuadroHorario();
        dadosDestino.setPeriodoAtividadeQuadro(destinoPeriodo);

        when(quadroRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(alocacaoRepository.findByQuadroHorarioId(1L)).thenReturn(List.of(new Alocacao()));
        when(quadroRepository.findTopByTurmaIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(8L, 20L))
                .thenReturn(Optional.of(ultimaVersaoDestino));
        when(quadroRepository.save(any(QuadroHorario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(alocacaoRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        QuadroHorario resultado = useCase.executar(1L, dadosDestino);

        assertThat(resultado.getVersao()).isEqualTo(5);
        assertThat(resultado.getTurma()).isSameAs(turma);
        assertThat(resultado.getPeriodoAtividadeQuadro()).isSameAs(destinoPeriodo);
        verify(quadroRepository).findTopByTurmaIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(8L, 20L);
    }

    @Test
    void iniciaVersaoUmQuandoDestinoAindaNaoTemQuadro() {
        Curso curso = new Curso();
        curso.setId(7L);
        Turma turma = new Turma();
        turma.setId(8L);
        turma.setCurso(curso);
        PeriodoAtividadeQuadro origemPeriodo = periodo(10L);
        PeriodoAtividadeQuadro destinoPeriodo = periodo(20L);
        QuadroHorario origem = quadro(1L, 3, turma, curso, origemPeriodo);
        QuadroHorario dadosDestino = new QuadroHorario();
        dadosDestino.setPeriodoAtividadeQuadro(destinoPeriodo);

        when(quadroRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(alocacaoRepository.findByQuadroHorarioId(1L)).thenReturn(List.of(new Alocacao()));
        when(quadroRepository.findTopByTurmaIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(8L, 20L))
                .thenReturn(Optional.empty());
        when(quadroRepository.save(any(QuadroHorario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(alocacaoRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        QuadroHorario resultado = useCase.executar(1L, dadosDestino);

        assertThat(resultado.getVersao()).isEqualTo(1);
        assertThat(resultado.getPeriodoAtividadeQuadro()).isSameAs(destinoPeriodo);
    }

    @Test
    void naoPersisteCopiaQuandoDestinoJaTemQuadroAtivo() {
        Curso curso = new Curso();
        curso.setId(7L);
        Turma turma = new Turma();
        turma.setId(8L);
        turma.setCurso(curso);
        PeriodoAtividadeQuadro origemPeriodo = periodo(10L);
        PeriodoAtividadeQuadro destinoPeriodo = periodo(20L);
        QuadroHorario origem = quadro(1L, 3, turma, curso, origemPeriodo);
        QuadroHorario dadosDestino = new QuadroHorario();
        dadosDestino.setPeriodoAtividadeQuadro(destinoPeriodo);

        when(quadroRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(alocacaoRepository.findByQuadroHorarioId(1L)).thenReturn(List.of(new Alocacao()));
        when(quadroRepository.findTopByTurmaIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(8L, 20L))
                .thenReturn(Optional.empty());
        doThrow(new BusinessException(
                "Já existe um quadro horário ativo para este curso no período atividade quadro selecionado."))
                .when(validarQuadroValido).validarQuadroAtivoDuplicado(any(QuadroHorario.class));

        assertThatThrownBy(() -> useCase.executar(1L, dadosDestino))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe um quadro horário ativo para este curso no período atividade quadro selecionado.");
        verify(quadroRepository, never()).save(any(QuadroHorario.class));
        verify(alocacaoRepository, never()).saveAll(anyList());
    }

    private PeriodoAtividadeQuadro periodo(long id) {
        PeriodoAtividadeQuadro periodo = new PeriodoAtividadeQuadro();
        periodo.setId(id);
        periodo.setStatus(Status.ATIVO);
        return periodo;
    }

    private QuadroHorario quadro(long id, int versao, Turma turma, Curso curso,
            PeriodoAtividadeQuadro periodo) {
        QuadroHorario quadro = new QuadroHorario();
        quadro.setId(id);
        quadro.setVersao(versao);
        quadro.setStatus(Status.ATIVO);
        quadro.setTurma(turma);
        quadro.setCurso(curso);
        quadro.setPeriodoAtividadeQuadro(periodo);
        return quadro;
    }
}

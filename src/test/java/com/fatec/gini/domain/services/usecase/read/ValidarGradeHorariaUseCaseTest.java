package com.fatec.gini.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.PeriodoAtividadeQuadro;
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ValidarQuadroHorarioUseCaseTest {

    @InjectMocks
    private ValidarQuadroHorarioUseCase useCase;

    @Mock
    private QuadroHorarioRepository quadroHorarioRepository;

    @Test
    void naoDeveLancarExcecaoQuandoQuadroAtivaEUltimaVersao() {
        QuadroHorario quadro = criarQuadroHorario(10L, 1L, 2L, Status.ATIVO);
        Alocacao alocacao = new Alocacao();
        alocacao.setQuadroHorario(quadro);

        when(quadroHorarioRepository.findTopByCursoIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(1L, 2L))
                .thenReturn(Optional.of(criarQuadroHorario(10L, 1L, 2L, Status.ATIVO)));

        assertDoesNotThrow(() -> useCase.executar(alocacao));
    }

    @Test
    void deveLancarExcecaoQuandoQuadroInativa() {
        QuadroHorario quadro = criarQuadroHorario(10L, 1L, 2L, Status.INATIVO);
        Alocacao alocacao = new Alocacao();
        alocacao.setQuadroHorario(quadro);

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
    }

    @Test
    void deveLancarExcecaoQuandoNaoForUltimaVersao() {
        QuadroHorario quadroAtual = criarQuadroHorario(10L, 1L, 2L, Status.ATIVO);
        Alocacao alocacao = new Alocacao();
        alocacao.setQuadroHorario(quadroAtual);

        when(quadroHorarioRepository.findTopByCursoIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(1L, 2L))
                .thenReturn(Optional.of(criarQuadroHorario(11L, 1L, 2L, Status.ATIVO)));

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrarUltimaVersao() {
        QuadroHorario quadroAtual = criarQuadroHorario(10L, 1L, 2L, Status.ATIVO);
        Alocacao alocacao = new Alocacao();
        alocacao.setQuadroHorario(quadroAtual);

        when(quadroHorarioRepository.findTopByCursoIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(1L, 2L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
    }

    private QuadroHorario criarQuadroHorario(Long id, Long cursoId, Long periodoId, Status status) {
        QuadroHorario quadroHorario = new QuadroHorario();
        Curso curso = new Curso();
        curso.setId(cursoId);
        PeriodoAtividadeQuadro periodoAtividadeQuadro = new PeriodoAtividadeQuadro();
        periodoAtividadeQuadro.setId(periodoId);

        quadroHorario.setCurso(curso);
        quadroHorario.setPeriodoAtividadeQuadro(periodoAtividadeQuadro);
        quadroHorario.setStatus(status);

        try {
            java.lang.reflect.Field idField = QuadroHorario.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(quadroHorario, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return quadroHorario;
    }
}

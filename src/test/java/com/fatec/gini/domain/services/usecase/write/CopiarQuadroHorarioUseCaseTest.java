package com.fatec.gini.domain.services.usecase.write;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import com.fatec.gini.domain.entities.Status;
import com.fatec.gini.domain.services.usecase.read.ValidarCopiarQuadroHorarioUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class CopiarQuadroHorarioUseCaseTest {

    @InjectMocks
    private CopiarQuadroHorarioUseCase useCase;

    @Mock
    private QuadroHorarioRepository gradeHorariaRepository;

    @Mock
    private AlocacaoRepository alocacaoRepository;

    @Mock
    private ValidarCopiarQuadroHorarioUseCase validarCopiaGradeHorariaUseCase;

    @Mock
    private ValidarQuadroHorarioValidoUseCase validarGradeHorariaValidaUseCase;

    @Test
    void deveLancarExcecaoQuandoQuadroOrigemNaoEncontrado() {
        Long idOrigem = 1L;
        QuadroHorario dadosNovaGrade = new QuadroHorario();
        dadosNovaGrade.setVersao(1);

        List<Alocacao> alocacoes = new ArrayList<>();
        when(alocacaoRepository.findByQuadroHorarioId(idOrigem)).thenReturn(alocacoes);
        doNothing().when(validarCopiaGradeHorariaUseCase).validarRegrasParaCopiaDeGrade(alocacoes);
        when(gradeHorariaRepository.findById(idOrigem)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> useCase.executar(idOrigem, dadosNovaGrade));

        verify(gradeHorariaRepository, never()).save(any());
        verify(alocacaoRepository, never()).saveAll(any());
    }

    @Test
    void deveLancarExcecaoQuandoValidacaoDeCopiaFalhar() {
        Long idOrigem = 1L;
        QuadroHorario dadosNovaGrade = new QuadroHorario();

        List<Alocacao> alocacoes = new ArrayList<>();
        when(alocacaoRepository.findByQuadroHorarioId(idOrigem)).thenReturn(alocacoes);
        doThrow(new BusinessException("Erro de validação"))
                .when(validarCopiaGradeHorariaUseCase).validarRegrasParaCopiaDeGrade(alocacoes);

        assertThrows(BusinessException.class, () -> useCase.executar(idOrigem, dadosNovaGrade));

        verify(gradeHorariaRepository, never()).findById(any());
        verify(gradeHorariaRepository, never()).save(any());
    }

    @Test
    void deveCopiarQuadroHorarioComSucesso() {
        Long idOrigem = 1L;
        
        Curso curso = new Curso();
        curso.setId(10L);

        PeriodoAtividadeQuadro periodo = new PeriodoAtividadeQuadro();
        periodo.setId(20L);

        QuadroHorario gradeAnterior = new QuadroHorario();
        gradeAnterior.setId(idOrigem);
        gradeAnterior.setCurso(curso);
        gradeAnterior.setVersao(2);
        gradeAnterior.setPeriodoAtividadeQuadro(periodo);

        QuadroHorario dadosNovaGrade = new QuadroHorario();
        dadosNovaGrade.setVersao(2);
        dadosNovaGrade.setPeriodoAtividadeQuadro(periodo);

        Alocacao antigaAlocacao = new Alocacao();
        antigaAlocacao.setId(100L);

        List<Alocacao> alocacoesAnteriores = List.of(antigaAlocacao);

        when(alocacaoRepository.findByQuadroHorarioId(idOrigem)).thenReturn(alocacoesAnteriores);
        doNothing().when(validarCopiaGradeHorariaUseCase).validarRegrasParaCopiaDeGrade(alocacoesAnteriores);
        when(gradeHorariaRepository.findById(idOrigem)).thenReturn(Optional.of(gradeAnterior));
        
        doNothing().when(validarGradeHorariaValidaUseCase).validarQuadroAtivoDuplicado(any(QuadroHorario.class));
        
        // Mock save returning the saved object (which typically gets assigned an ID)
        when(gradeHorariaRepository.save(any(QuadroHorario.class))).thenAnswer(invocation -> {
            QuadroHorario q = invocation.getArgument(0);
            q.setId(2L);
            return q;
        });

        QuadroHorario novoQuadro = useCase.executar(idOrigem, dadosNovaGrade);

        assertNotNull(novoQuadro);
        assertEquals(3, novoQuadro.getVersao());
        assertEquals(Status.ATIVO, novoQuadro.getStatus());
        assertEquals(curso, novoQuadro.getCurso());
        assertEquals(periodo, novoQuadro.getPeriodoAtividadeQuadro());

        verify(gradeHorariaRepository).save(any(QuadroHorario.class));
        verify(alocacaoRepository).saveAll(any());
    }
}

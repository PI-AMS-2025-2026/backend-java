package com.fatec.horario.domain.services.usecase.write;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.infrastructure.repositories.AlocacaoRepository;
import com.fatec.horario.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class DuplicidadeAlocacaoUseCaseTest {

    @Mock
    private AlocacaoRepository alocacaoRepository;

    @InjectMocks
    private DuplicidadeAlocacaoUseCase duplicidadeAlocacaoUseCase;

    @Test
    void deveLancarExcecaoQuandoExistirDuplicidadeNaCriacao() {
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L, 4L, 5L, null);
        when(alocacaoRepository.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioId(
                1L, 2L, 3L, 4L, 5L)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> duplicidadeAlocacaoUseCase.validarNaoExisteDuplicidadeParaCriacao(alocacao));

        assertEquals("Já existe uma alocação cadastrada com os mesmos dados informados.", exception.getMessage());
    }

    @Test
    void naoDeveLancarExcecaoQuandoNaoExistirDuplicidadeNaCriacao() {
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L, 4L, 5L, null);
        when(alocacaoRepository.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioId(
                1L, 2L, 3L, 4L, 5L)).thenReturn(false);

        assertDoesNotThrow(() -> duplicidadeAlocacaoUseCase.validarNaoExisteDuplicidadeParaCriacao(alocacao));

        verify(alocacaoRepository).existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioId(
                1L, 2L, 3L, 4L, 5L);
    }

    @Test
    void deveLancarExcecaoQuandoExistirDuplicidadeNaAtualizacao() {
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L, 4L, 5L, 9L);
        when(alocacaoRepository.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioIdAndIdNot(
                1L, 2L, 3L, 4L, 5L, 9L)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> duplicidadeAlocacaoUseCase.validarNaoExisteDuplicidadeParaAtualizacao(alocacao));

        assertEquals("Já existe uma alocação cadastrada com os mesmos dados informados.", exception.getMessage());
    }

    @Test
    void naoDeveLancarExcecaoQuandoNaoExistirDuplicidadeNaAtualizacao() {
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L, 4L, 5L, 9L);
        when(alocacaoRepository.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioIdAndIdNot(
                1L, 2L, 3L, 4L, 5L, 9L)).thenReturn(false);

        assertDoesNotThrow(
                () -> duplicidadeAlocacaoUseCase.validarNaoExisteDuplicidadeParaAtualizacao(alocacao));

        verify(alocacaoRepository).existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioIdAndIdNot(
                1L, 2L, 3L, 4L, 5L, 9L);
    }

    private Alocacao criarAlocacao(Long turmaId, Long disciplinaId, Long salaId, Long diaSemanaId, Long horarioId,
            Long alocacaoId) {
        Alocacao alocacao = new Alocacao();
        alocacao.setId(alocacaoId);

        Turma turma = new Turma();
        turma.setId(turmaId);
        alocacao.setTurma(turma);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(disciplinaId);
        alocacao.setDisciplina(disciplina);

        Sala sala = new Sala();
        sala.setId(salaId);
        alocacao.setSala(sala);

        DiaSemana diaSemana = new DiaSemana();
        diaSemana.setId(diaSemanaId);
        alocacao.setDiaSemana(diaSemana);

        Horario horario = new Horario();
        horario.setId(horarioId);
        alocacao.setHorario(horario);

        return alocacao;
    }
}

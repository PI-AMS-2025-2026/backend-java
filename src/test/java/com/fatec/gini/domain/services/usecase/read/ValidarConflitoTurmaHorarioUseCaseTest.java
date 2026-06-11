package com.fatec.gini.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ValidarConflitoTurmaHorarioUseCaseTest {

    @InjectMocks
    private ValidarConflitoTurmaBlocoHorarioUseCase useCase;

    @Mock
    private AlocacaoRepository repository;

    @Test
    void naoDeveLancarExcecaoQuandoNaoHaConflito() {
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L);

        when(repository.existsConflitoTurmaHorario(1L, 2L, 3L)).thenReturn(false);

        assertDoesNotThrow(() -> useCase.executar(alocacao));
        verify(repository).existsConflitoTurmaHorario(1L, 2L, 3L);
    }

    @Test
    void deveLancarExcecaoQuandoHaConflito() {
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L);

        when(repository.existsConflitoTurmaHorario(1L, 2L, 3L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
        verify(repository).existsConflitoTurmaHorario(1L, 2L, 3L);
    }

    private Alocacao criarAlocacao(Long turmaId, Long diaSemanaId, Long horarioId) {
        Turma turma = new Turma();
        turma.setId(turmaId);

        DiaSemana diaSemana = new DiaSemana();
        diaSemana.setId(diaSemanaId);

        BlocoHorario horario = new BlocoHorario();
        horario.setId(horarioId);

        Alocacao alocacao = new Alocacao();
        alocacao.setTurma(turma);
        alocacao.setDiaSemana(diaSemana);
        alocacao.setBlocoHorario(horario);
        return alocacao;
    }
}

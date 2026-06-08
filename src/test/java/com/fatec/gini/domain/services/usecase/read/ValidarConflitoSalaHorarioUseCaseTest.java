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
import com.fatec.gini.domain.entities.Horario;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ValidarConflitoSalaHorarioUseCaseTest {

    // Injeta automaticamente os mocks dentro do use case testado
    @InjectMocks
    private ValidarConflitoSalaHorarioUseCase useCase;

    // Simula o comportamento do repository
    @Mock
    private AlocacaoRepository repository;

    @Test
    void naoDeveLancarExcecaoQuandoNaoHaConflito() {

        // Cria uma alocação de exemplo
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L);

        // Simula que NÃO existe conflito de sala no banco
        when(repository.existsBySalaIdAndDiaSemanaIdAndHorarioId(
                1L, 2L, 3L)).thenReturn(false);

        // Verifica se nenhuma exceção será lançada
        assertDoesNotThrow(() -> useCase.executar(alocacao));

        // Verifica se o repository foi chamado corretamente
        verify(repository).existsBySalaIdAndDiaSemanaIdAndHorarioId(
                1L, 2L, 3L);
    }

    @Test
    void deveLancarExcecaoQuandoHaConflito() {

        // Cria uma alocação de exemplo
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L);

        // Simula que JÁ existe uma sala ocupada neste horário
        when(repository.existsBySalaIdAndDiaSemanaIdAndHorarioId(
                1L, 2L, 3L)).thenReturn(true);

        // Verifica se a exceção de negócio será lançada
        assertThrows(
                BusinessException.class,
                () -> useCase.executar(alocacao));

        // Verifica se o repository foi chamado corretamente
        verify(repository).existsBySalaIdAndDiaSemanaIdAndHorarioId(
                1L, 2L, 3L);
    }

    /**
     * Método auxiliar para criação de uma alocação mockada
     */
    private Alocacao criarAlocacao(
            Long salaId,
            Long diaSemanaId,
            Long horarioId) {

        // Cria sala
        Sala sala = new Sala();
        sala.setId(salaId);

        // Cria dia da semana
        DiaSemana diaSemana = new DiaSemana();
        diaSemana.setId(diaSemanaId);

        // Cria horário
        Horario horario = new Horario();
        horario.setId(horarioId);

        // Monta alocação
        Alocacao alocacao = new Alocacao();
        alocacao.setSala(sala);
        alocacao.setDiaSemana(diaSemana);
        alocacao.setHorario(horario);

        return alocacao;
    }
}
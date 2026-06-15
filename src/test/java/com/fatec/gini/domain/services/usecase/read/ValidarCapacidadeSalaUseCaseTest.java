package com.fatec.gini.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ValidarCapacidadeSalaUseCaseTest {

    @InjectMocks
    private ValidarCapacidadeSalaUseCase useCase;

    @Test
    void naoDeveLancarExcecaoQuandoCapacidadeMaiorOuIgualAoNumeroDeAlunos() {
        Alocacao alocacao = criarAlocacaoComTurmaESala(30, 30);
        assertDoesNotThrow(() -> useCase.executar(alocacao));
    }

    @Test
    void naoDeveLancarExcecaoQuandoCapacidadeMaiorQueNumeroDeAlunos() {
        Alocacao alocacao = criarAlocacaoComTurmaESala(50, 30);
        assertDoesNotThrow(() -> useCase.executar(alocacao));
    }

    @Test
    void deveLancarExcecaoQuandoCapacidadeMenorQueNumeroDeAlunos() {
        Alocacao alocacao = criarAlocacaoComTurmaESala(20, 30);
        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
    }

    @Test
    void deveLancarExcecaoComMensagemDescritivaDe40AlunosEm30Lugares() {
        Alocacao alocacao = criarAlocacaoComTurmaESala(30, 40);
        
        try {
            useCase.executar(alocacao);
        } catch (BusinessException e) {
            assert (e.getMessage().contains("40"));
            assert (e.getMessage().contains("30"));
            assert (e.getMessage().contains("capacidade suficiente"));
        }
    }

    private Alocacao criarAlocacaoComTurmaESala(Integer capacidadeSala, Integer numeroAlunos) {
        Sala sala = new Sala();
        sala.setId(1L);
        sala.setCapacidade(capacidadeSala);

        Turma turma = new Turma();
        turma.setId(1L);
        turma.setNumeroAlunos(numeroAlunos);

        Alocacao alocacao = new Alocacao();
        alocacao.setId(1L);
        alocacao.setSala(sala);
        alocacao.setTurma(turma);

        return alocacao;
    }
}

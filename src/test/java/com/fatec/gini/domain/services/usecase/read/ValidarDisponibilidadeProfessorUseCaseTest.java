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
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ValidarDisponibilidadeProfessorUseCaseTest {

    @InjectMocks
    private ValidarDisponibilidadeProfessorUseCase useCase;

    @Mock
    private AlocacaoRepository alocacaoRepository;

    @Mock
    private DisponibilidadeProfessorRepository disponibilidadeProfessorRepository;

    @Test
    void naoDeveLancarExcecaoQuandoProfessorEstaDisponivelESemSobreposicao() {
        Alocacao alocacao = criarAlocacao(10L, 20L, 30L);

        when(disponibilidadeProfessorRepository.verificarDisponibilidadeProfessor(10L, 20L, 30L)).thenReturn(true);
        when(alocacaoRepository.existsByProfessorIdAndDiaSemanaIdAndBlocoHorarioId(10L, 20L, 30L)).thenReturn(false);

        assertDoesNotThrow(() -> useCase.executar(alocacao));
    }

    @Test
    void deveLancarExcecaoQuandoProfessorNaoTemDisponibilidade() {
        Alocacao alocacao = criarAlocacao(10L, 20L, 30L);

        when(disponibilidadeProfessorRepository.verificarDisponibilidadeProfessor(10L, 20L, 30L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
        verify(disponibilidadeProfessorRepository).verificarDisponibilidadeProfessor(10L, 20L, 30L);
    }

    @Test
    void deveLancarExcecaoQuandoProfessorTemSobreposicaoDeAtividades() {
        Alocacao alocacao = criarAlocacao(10L, 20L, 30L);

        when(disponibilidadeProfessorRepository.verificarDisponibilidadeProfessor(10L, 20L, 30L)).thenReturn(true);
        when(alocacaoRepository.existsByProfessorIdAndDiaSemanaIdAndBlocoHorarioId(10L, 20L, 30L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
        verify(alocacaoRepository).existsByProfessorIdAndDiaSemanaIdAndBlocoHorarioId(10L, 20L, 30L);
    }

    private Alocacao criarAlocacao(Long professorId, Long diaSemanaId, Long horarioId) {
        Professor professor = new Professor();
        professor.setId(professorId);

        DiaSemana diaSemana = new DiaSemana();
        diaSemana.setId(diaSemanaId);

        BlocoHorario horario = new BlocoHorario();
        horario.setId(horarioId);

        Alocacao alocacao = new Alocacao();
        alocacao.setProfessor(professor);
        alocacao.setDiaSemana(diaSemana);
        alocacao.setBlocoHorario(horario);
        return alocacao;
    }
}

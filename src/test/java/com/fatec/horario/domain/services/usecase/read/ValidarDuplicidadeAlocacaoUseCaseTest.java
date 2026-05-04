package com.fatec.horario.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
class ValidarDuplicidadeAlocacaoUseCaseTest {

    @InjectMocks
    private ValidarDuplicidadeAlocacaoUseCase useCase;

    @Mock
    private AlocacaoRepository alocacaoRepository;

    @Test
    void naoDeveLancarExcecaoNaCriacaoQuandoNaoHaDuplicidade() {
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L, 4L, 5L, null);

        when(alocacaoRepository.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioId(
                1L, 2L, 3L, 4L, 5L)).thenReturn(false);

        assertDoesNotThrow(() -> useCase.executarCriacao(alocacao));
    }

    @Test
    void deveLancarExcecaoNaCriacaoQuandoHaDuplicidade() {
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L, 4L, 5L, null);

        when(alocacaoRepository.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioId(
                1L, 2L, 3L, 4L, 5L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> useCase.executarCriacao(alocacao));
    }

    @Test
    void naoDeveLancarExcecaoNaAtualizacaoQuandoNaoHaDuplicidade() {
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L, 4L, 5L, 99L);

        when(alocacaoRepository.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioIdAndIdNot(
                1L, 2L, 3L, 4L, 5L, 99L)).thenReturn(false);

        assertDoesNotThrow(() -> useCase.executarAtualizacao(alocacao));
    }

    @Test
    void deveLancarExcecaoNaAtualizacaoQuandoHaDuplicidade() {
        Alocacao alocacao = criarAlocacao(1L, 2L, 3L, 4L, 5L, 99L);

        when(alocacaoRepository.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaIdAndHorarioIdAndIdNot(
                1L, 2L, 3L, 4L, 5L, 99L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> useCase.executarAtualizacao(alocacao));
    }

    private Alocacao criarAlocacao(Long turmaId, Long disciplinaId, Long salaId, Long diaSemanaId, Long horarioId,
            Long idAlocacao) {
        Turma turma = new Turma();
        turma.setId(turmaId);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(disciplinaId);

        Sala sala = new Sala();
        sala.setId(salaId);

        DiaSemana diaSemana = new DiaSemana();
        diaSemana.setId(diaSemanaId);

        Horario horario = new Horario();
        horario.setId(horarioId);

        Alocacao alocacao = new Alocacao();
        alocacao.setId(idAlocacao);
        alocacao.setTurma(turma);
        alocacao.setDisciplina(disciplina);
        alocacao.setSala(sala);
        alocacao.setDiaSemana(diaSemana);
        alocacao.setHorario(horario);
        return alocacao;
    }
}

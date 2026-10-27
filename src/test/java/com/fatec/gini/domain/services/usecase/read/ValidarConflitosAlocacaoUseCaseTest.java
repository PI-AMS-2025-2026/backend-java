package com.fatec.gini.domain.services.usecase.read;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ValidarConflitosAlocacaoUseCaseTest {

    @Mock
    private AlocacaoRepository repository;

    private Alocacao alocacao;

    @BeforeEach
    void setUp() {
        Turma turma = new Turma();
        turma.setId(1L);
        Disciplina disciplina = new Disciplina();
        disciplina.setId(2L);
        Sala sala = new Sala();
        sala.setId(3L);
        BlocoHorario bloco = new BlocoHorario();
        bloco.setId(4L);
        alocacao = new Alocacao(turma, disciplina, sala, null, DiaSemana.SEGUNDA, bloco, null);
        alocacao.setId(10L);
    }

    @Test
    void deveAceitarSalaLivre() {
        when(repository.existsBySalaIdAndDiaSemanaAndBlocoHorarioId(3L, DiaSemana.SEGUNDA, 4L))
                .thenReturn(false);

        assertThatCode(() -> new ValidarConflitoSalaBlocoHorarioUseCase(repository).executar(alocacao))
                .doesNotThrowAnyException();
        verify(repository).existsBySalaIdAndDiaSemanaAndBlocoHorarioId(3L, DiaSemana.SEGUNDA, 4L);
    }

    @Test
    void deveRejeitarSalaOcupada() {
        when(repository.existsBySalaIdAndDiaSemanaAndBlocoHorarioId(3L, DiaSemana.SEGUNDA, 4L))
                .thenReturn(true);

        assertThrows(BusinessException.class,
                () -> new ValidarConflitoSalaBlocoHorarioUseCase(repository).executar(alocacao));
    }

    @Test
    void deveRejeitarConflitoDeTurma() {
        when(repository.existsConflitoTurmaHorario(1L, DiaSemana.SEGUNDA, 4L)).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> new ValidarConflitoTurmaBlocoHorarioUseCase(repository).executar(alocacao));
    }

    @Test
    void deveAceitarTurmaSemConflito() {
        when(repository.existsConflitoTurmaHorario(1L, DiaSemana.SEGUNDA, 4L)).thenReturn(false);

        assertThatCode(() -> new ValidarConflitoTurmaBlocoHorarioUseCase(repository).executar(alocacao))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarDuplicidadeNaCriacao() {
        when(repository.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaAndBlocoHorarioId(
                1L, 2L, 3L, DiaSemana.SEGUNDA, 4L)).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> new ValidarDuplicidadeAlocacaoUseCase(repository).executarCriacao(alocacao));
    }

    @Test
    void deveIgnorarPropriaAlocacaoNaAtualizacaoQuandoNaoHouverOutraDuplicada() {
        when(repository.existsByTurmaIdAndDisciplinaIdAndSalaIdAndDiaSemanaAndBlocoHorarioIdAndIdNot(
                1L, 2L, 3L, DiaSemana.SEGUNDA, 4L, 10L)).thenReturn(false);

        assertThatCode(() -> new ValidarDuplicidadeAlocacaoUseCase(repository).executarAtualizacao(alocacao))
                .doesNotThrowAnyException();
    }
}
package com.fatec.gini.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

class ValidarCargaHorariaMaximaProfessorUseCaseTest {

    @Test
    void devePermitirQuandoCargaHorariaDiariaExataOitoHoras() {
        AlocacaoRepository repository = Mockito.mock(AlocacaoRepository.class);
        ValidarCargaHorariaMaximaProfessorUseCase useCase = new ValidarCargaHorariaMaximaProfessorUseCase(repository);

        Professor professor = new Professor();
        professor.setId(1L);

        Alocacao alocacao = new Alocacao();
        alocacao.setProfessor(professor);
        alocacao.setDiaSemana(DiaSemana.SEGUNDA);
        alocacao.setBlocoHorario(new BlocoHorario(LocalTime.of(8, 0), LocalTime.of(16, 0), 480));

        when(repository.findByProfessorIdAndDiaSemana(1L, DiaSemana.SEGUNDA)).thenReturn(List.of());
        when(repository.findByProfessorId(1L)).thenReturn(List.of());

        assertDoesNotThrow(() -> useCase.validar(1L, DiaSemana.SEGUNDA, alocacao));
    }

    @Test
    void deveRejeitarQuandoCargaHorariaDiariaUltrapassaOitoHoras() {
        AlocacaoRepository repository = Mockito.mock(AlocacaoRepository.class);
        ValidarCargaHorariaMaximaProfessorUseCase useCase = new ValidarCargaHorariaMaximaProfessorUseCase(repository);

        Professor professor = new Professor();
        professor.setId(1L);

        Alocacao alocacao = new Alocacao();
        alocacao.setProfessor(professor);
        alocacao.setDiaSemana(DiaSemana.SEGUNDA);
        alocacao.setBlocoHorario(new BlocoHorario(LocalTime.of(8, 0), LocalTime.of(17, 0), 540));

        when(repository.findByProfessorIdAndDiaSemana(1L, DiaSemana.SEGUNDA)).thenReturn(List.of());
        when(repository.findByProfessorId(1L)).thenReturn(List.of());

        assertThrows(BusinessException.class, () -> useCase.validar(1L, DiaSemana.SEGUNDA, alocacao));
    }

    @Test
    void devePermitirQuandoAtualizaUmaAlocacaoExistenteMantendoMesmaCarga() {
        AlocacaoRepository repository = Mockito.mock(AlocacaoRepository.class);
        ValidarCargaHorariaMaximaProfessorUseCase useCase = new ValidarCargaHorariaMaximaProfessorUseCase(repository);

        Professor professor = new Professor();
        professor.setId(1L);

        Alocacao alocacaoExistente = new Alocacao();
        alocacaoExistente.setId(10L);
        alocacaoExistente.setProfessor(professor);
        alocacaoExistente.setDiaSemana(DiaSemana.SEGUNDA);
        alocacaoExistente.setBlocoHorario(new BlocoHorario(LocalTime.of(8, 0), LocalTime.of(12, 0), 240));

        Alocacao alocacaoAtualizada = new Alocacao();
        alocacaoAtualizada.setId(10L);
        alocacaoAtualizada.setProfessor(professor);
        alocacaoAtualizada.setDiaSemana(DiaSemana.SEGUNDA);
        alocacaoAtualizada.setBlocoHorario(new BlocoHorario(LocalTime.of(8, 0), LocalTime.of(12, 0), 240));

        when(repository.findByProfessorIdAndDiaSemana(1L, DiaSemana.SEGUNDA)).thenReturn(List.of(alocacaoExistente));
        when(repository.findByProfessorId(1L)).thenReturn(List.of(alocacaoExistente));

        assertDoesNotThrow(() -> useCase.validar(1L, DiaSemana.SEGUNDA, alocacaoAtualizada));
    }

    @Test
    void deveRejeitarQuandoAtualizacaoAumentaCargaParaMaisDeOitoHoras() {
        AlocacaoRepository repository = Mockito.mock(AlocacaoRepository.class);
        ValidarCargaHorariaMaximaProfessorUseCase useCase = new ValidarCargaHorariaMaximaProfessorUseCase(repository);

        Professor professor = new Professor();
        professor.setId(1L);

        Alocacao alocacaoExistente = new Alocacao();
        alocacaoExistente.setId(10L);
        alocacaoExistente.setProfessor(professor);
        alocacaoExistente.setDiaSemana(DiaSemana.SEGUNDA);
        alocacaoExistente.setBlocoHorario(new BlocoHorario(LocalTime.of(8, 0), LocalTime.of(16, 0), 480));

        Alocacao alocacaoAtualizada = new Alocacao();
        alocacaoAtualizada.setId(10L);
        alocacaoAtualizada.setProfessor(professor);
        alocacaoAtualizada.setDiaSemana(DiaSemana.SEGUNDA);
        alocacaoAtualizada.setBlocoHorario(new BlocoHorario(LocalTime.of(8, 0), LocalTime.of(17, 0), 540));

        when(repository.findByProfessorIdAndDiaSemana(1L, DiaSemana.SEGUNDA)).thenReturn(List.of(alocacaoExistente));
        when(repository.findByProfessorId(1L)).thenReturn(List.of(alocacaoExistente));

        assertThrows(BusinessException.class, () -> useCase.validar(1L, DiaSemana.SEGUNDA, alocacaoAtualizada));
    }

    @Test
    void devePermitirInterjornadaEntreSextaESegundaMesmoComFimDeSemana() {
        AlocacaoRepository repository = Mockito.mock(AlocacaoRepository.class);
        ValidarCargaHorariaMaximaProfessorUseCase useCase = new ValidarCargaHorariaMaximaProfessorUseCase(repository);
        Professor professor = new Professor();
        professor.setId(1L);

        Alocacao sexta = new Alocacao();
        sexta.setProfessor(professor);
        sexta.setDiaSemana(DiaSemana.SEXTA);
        sexta.setBlocoHorario(new BlocoHorario(LocalTime.of(20, 0), LocalTime.of(22, 0), 120));
        Alocacao segunda = new Alocacao();
        segunda.setProfessor(professor);
        segunda.setDiaSemana(DiaSemana.SEGUNDA);
        segunda.setBlocoHorario(new BlocoHorario(LocalTime.of(8, 0), LocalTime.of(10, 0), 120));

        when(repository.findByProfessorId(1L)).thenReturn(List.of(sexta));
        when(repository.findByProfessorIdAndDiaSemana(1L, DiaSemana.SEGUNDA)).thenReturn(List.of());

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> useCase.validar(1L, DiaSemana.SEGUNDA, segunda));
    }

    @Test
    void devePermitirInterjornadaEntreSextaESegundaMesmoComAulaTardeNaSexta() {
        AlocacaoRepository repository = Mockito.mock(AlocacaoRepository.class);
        ValidarCargaHorariaMaximaProfessorUseCase useCase = new ValidarCargaHorariaMaximaProfessorUseCase(repository);
        Professor professor = new Professor();
        professor.setId(1L);

        Alocacao sexta = new Alocacao();
        sexta.setProfessor(professor);
        sexta.setDiaSemana(DiaSemana.SEXTA);
        sexta.setBlocoHorario(new BlocoHorario(LocalTime.of(23, 0), LocalTime.of(23, 59), 59));
        Alocacao segunda = new Alocacao();
        segunda.setProfessor(professor);
        segunda.setDiaSemana(DiaSemana.SEGUNDA);
        segunda.setBlocoHorario(new BlocoHorario(LocalTime.of(8, 0), LocalTime.of(10, 0), 120));

        when(repository.findByProfessorId(1L)).thenReturn(List.of(sexta));
        when(repository.findByProfessorIdAndDiaSemana(1L, DiaSemana.SEGUNDA)).thenReturn(List.of());

        assertDoesNotThrow(() -> useCase.validar(1L, DiaSemana.SEGUNDA, segunda));
    }
}

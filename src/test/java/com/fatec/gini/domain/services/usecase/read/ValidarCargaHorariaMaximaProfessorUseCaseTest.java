package com.fatec.gini.domain.services.usecase.read;

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
    void deveRejeitarQuandoAAlocacaoAtualExcedeOLimiteDiario() {
        AlocacaoRepository repository = Mockito.mock(AlocacaoRepository.class);
        ValidarCargaHorariaMaximaProfessorUseCase useCase = new ValidarCargaHorariaMaximaProfessorUseCase(repository);

        Professor professor = new Professor();
        professor.setId(1L);

        Alocacao alocacaoExistente = new Alocacao();
        alocacaoExistente.setProfessor(professor);
        alocacaoExistente.setDiaSemana(DiaSemana.SEGUNDA);
        alocacaoExistente.setBlocoHorario(new BlocoHorario(LocalTime.of(8, 0), LocalTime.of(16, 0), 480));

        Alocacao novaAlocacao = new Alocacao();
        novaAlocacao.setProfessor(professor);
        novaAlocacao.setDiaSemana(DiaSemana.SEGUNDA);
        novaAlocacao.setBlocoHorario(new BlocoHorario(LocalTime.of(16, 0), LocalTime.of(17, 0), 60));

        when(repository.findByProfessorAndDiaSemana(1L, DiaSemana.SEGUNDA)).thenReturn(List.of(alocacaoExistente));
        when(repository.buscarUltimaAulaDoDia(1L, DiaSemana.DOMINGO)).thenReturn(List.of());

        assertThrows(BusinessException.class, () -> useCase.validar(1L, DiaSemana.SEGUNDA, novaAlocacao));
    }
}

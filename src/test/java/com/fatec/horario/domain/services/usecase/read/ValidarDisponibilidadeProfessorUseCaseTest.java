package com.fatec.horario.domain.services.usecase.read;

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
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.services.usecase.read.ValidarDisponibilidadeProfessorUseCase;
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
        when(alocacaoRepository.existsByUsuarioIdAndDiaSemanaIdAndHorarioId(10L, 20L, 30L)).thenReturn(false);

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
        when(alocacaoRepository.existsByUsuarioIdAndDiaSemanaIdAndHorarioId(10L, 20L, 30L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
        verify(alocacaoRepository).existsByUsuarioIdAndDiaSemanaIdAndHorarioId(10L, 20L, 30L);
    }

    private Alocacao criarAlocacao(Long usuarioId, Long diaSemanaId, Long horarioId) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        DiaSemana diaSemana = new DiaSemana();
        diaSemana.setId(diaSemanaId);

        Horario horario = new Horario();
        horario.setId(horarioId);

        Alocacao alocacao = new Alocacao();
        alocacao.setUsuario(usuario);
        alocacao.setDiaSemana(diaSemana);
        alocacao.setHorario(horario);
        return alocacao;
    }
}

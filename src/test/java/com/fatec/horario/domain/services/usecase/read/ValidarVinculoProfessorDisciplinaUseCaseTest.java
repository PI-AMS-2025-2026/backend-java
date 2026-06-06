package com.fatec.horario.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.user.Usuario;
import com.fatec.gini.domain.services.usecase.read.ValidarVinculoProfessorDisciplinaUseCase;
import com.fatec.gini.infrastructure.repositories.ProfessorDisciplinaRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ValidarVinculoProfessorDisciplinaUseCaseTest {

    @InjectMocks
    private ValidarVinculoProfessorDisciplinaUseCase useCase;

    @Mock
    private ProfessorDisciplinaRepository repository;

    @Test
    void naoDeveLancarExcecaoQuandoProfessorPodeLecionarDisciplina() {
        Alocacao alocacao = criarAlocacao(10L, 20L);

        when(repository.existsByUsuarioIdAndDisciplinaId(10L, 20L)).thenReturn(true);

        assertDoesNotThrow(() -> useCase.executar(alocacao));
    }

    @Test
    void deveLancarExcecaoQuandoProfessorNaoPodeLecionarDisciplina() {
        Alocacao alocacao = criarAlocacao(10L, 20L);

        when(repository.existsByUsuarioIdAndDisciplinaId(10L, 20L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> useCase.executar(alocacao));
    }

    private Alocacao criarAlocacao(Long professorId, Long disciplinaId) {
        Usuario usuario = new Usuario();
        usuario.setId(professorId);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(disciplinaId);

        Alocacao alocacao = new Alocacao();
        alocacao.setUsuario(usuario);
        alocacao.setDisciplina(disciplina);
        return alocacao;
    }
}

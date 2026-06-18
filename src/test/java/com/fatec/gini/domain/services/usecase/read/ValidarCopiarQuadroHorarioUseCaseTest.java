package com.fatec.gini.domain.services.usecase.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.TipoSala;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ValidarCopiarQuadroHorarioUseCaseTest {

    @InjectMocks
    private ValidarCopiarQuadroHorarioUseCase useCase;

    @Mock
    private DisponibilidadeProfessorRepository disponibilidadeProfessorRepository;

    @Test
    void deveLancarExcecaoQuandoAlocacoesForVazia() {
        assertThrows(BusinessException.class, () -> 
            useCase.validarRegrasParaCopiaDeGrade(Collections.emptyList())
        );
    }

    @Test
    void deveLancarExcecaoQuandoFaltaDadosObrigatorios() {
        Alocacao alocacaoComErro = criarAlocacaoCompleta(1L, 2L, 3L, 4L, 5L, 6L);
        alocacaoComErro.setProfessor(null); // Dado obrigatório ausente

        assertThrows(BusinessException.class, () -> 
            useCase.validarRegrasParaCopiaDeGrade(List.of(alocacaoComErro))
        );
    }

    @Test
    void deveLancarExcecaoQuandoProfessorNaoTemDisponibilidade() {
        Alocacao alocacao = criarAlocacaoCompleta(1L, 2L, 3L, 4L, 5L, 5L);

        // Nenhuma disponibilidade Mockada no BD para o professor 1L
        when(disponibilidadeProfessorRepository.findByProfessorIdIn(List.of(1L)))
                .thenReturn(Collections.emptyList());

        assertThrows(BusinessException.class, () -> 
            useCase.validarRegrasParaCopiaDeGrade(List.of(alocacao))
        );
    }

    @Test
    void deveLancarExcecaoQuandoTipoSalaForIncompativel() {
        Alocacao alocacao = criarAlocacaoCompleta(1L, 2L, 3L, 4L, 5L, 6L); // TipoSala disciplina = 5L, TipoSala sala = 6L
        
        // Simular disponibilidade válida
        DisponibilidadeProfessor disp = criarDisponibilidade(1L, 2L, 3L);
        when(disponibilidadeProfessorRepository.findByProfessorIdIn(List.of(1L)))
                .thenReturn(List.of(disp));

        assertThrows(BusinessException.class, () -> 
            useCase.validarRegrasParaCopiaDeGrade(List.of(alocacao))
        );
    }

    @Test
    void deveExecutarComSucessoQuandoTodosOsDadosSaoValidos() {
        Alocacao alocacao = criarAlocacaoCompleta(1L, 2L, 3L, 4L, 5L, 5L); // TipoSala disciplina = 5L, TipoSala sala = 5L

        DisponibilidadeProfessor disp = criarDisponibilidade(1L, 2L, 3L);
        when(disponibilidadeProfessorRepository.findByProfessorIdIn(List.of(1L)))
                .thenReturn(List.of(disp));

        assertDoesNotThrow(() -> 
            useCase.validarRegrasParaCopiaDeGrade(List.of(alocacao))
        );
    }

    private Alocacao criarAlocacaoCompleta(Long professorId, Long diaSemanaId, Long blocoHorarioId, 
                                           Long salaId, Long tipoSalaDisciplinaId, Long tipoSalaSalaId) {
        Professor prof = new Professor();
        prof.setId(professorId);

        DiaSemana dia = new DiaSemana();
        dia.setId(diaSemanaId);

        BlocoHorario bloco = new BlocoHorario();
        bloco.setId(blocoHorarioId);

        TipoSala tipoSalaDisc = new TipoSala();
        tipoSalaDisc.setId(tipoSalaDisciplinaId);
        Disciplina disc = new Disciplina();
        disc.setTipoSala(tipoSalaDisc);

        TipoSala tipoSalaS = new TipoSala();
        tipoSalaS.setId(tipoSalaSalaId);
        Sala sala = new Sala();
        sala.setId(salaId);
        sala.setTipoSala(tipoSalaS);

        Alocacao alocacao = new Alocacao();
        alocacao.setProfessor(prof);
        alocacao.setDiaSemana(dia);
        alocacao.setBlocoHorario(bloco);
        alocacao.setDisciplina(disc);
        alocacao.setSala(sala);

        return alocacao;
    }

    private DisponibilidadeProfessor criarDisponibilidade(Long professorId, Long diaSemanaId, Long blocoHorarioId) {
        Professor prof = new Professor();
        prof.setId(professorId);

        DiaSemana dia = new DiaSemana();
        dia.setId(diaSemanaId);

        BlocoHorario bloco = new BlocoHorario();
        bloco.setId(blocoHorarioId);

        return new DisponibilidadeProfessor(prof, dia, bloco);
    }
}

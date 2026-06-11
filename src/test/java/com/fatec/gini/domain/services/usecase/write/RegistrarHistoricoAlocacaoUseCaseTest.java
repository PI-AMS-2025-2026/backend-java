package com.fatec.gini.domain.services.usecase.write;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.Disciplina;
import com.fatec.gini.domain.entities.GradeHoraria;
import com.fatec.gini.domain.entities.HistoricoAlteracao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.infrastructure.repositories.HistoricoAlteracaoRepository;

@ExtendWith(MockitoExtension.class)
class RegistrarHistoricoAlocacaoUseCaseTest {

    @InjectMocks
    private RegistrarHistoricoAlocacaoUseCase useCase;

    @Mock
    private HistoricoAlteracaoRepository historicoAlteracaoRepository;

    @Test
    void deveRegistrarCriacao() {
        Alocacao alocacao = criarAlocacao(10L, 1L, 2L, 3L, 4L, 5L, 6L, 7L);
        Usuario usuario = new Usuario();
        usuario.setId(99L);

        useCase.registrarCriacao(alocacao, usuario);

        ArgumentCaptor<HistoricoAlteracao> captor = ArgumentCaptor.forClass(HistoricoAlteracao.class);
        verify(historicoAlteracaoRepository).save(captor.capture());

        HistoricoAlteracao salvo = captor.getValue();
        assertEquals("Alocação_10", salvo.getCampoAlterado());
        assertEquals("Criação de alocação", salvo.getJustificativa());
        assertNotNull(salvo.getValorNovo());
        assertEquals(usuario, salvo.getUsuario());
        assertEquals(alocacao, salvo.getAlocacao());
    }

    @Test
    void deveRegistrarSomenteCamposAlteradosNaAtualizacao() {
        Alocacao alocacaoNova = criarAlocacao(10L, 1L, 20L, 3L, 4L, 5L, 6L, 7L);
        Alocacao alocacaoAntiga = criarAlocacao(10L, 1L, 2L, 3L, 40L, 5L, 6L, 7L);
        Usuario usuario = new Usuario();
        usuario.setId(99L);

        useCase.registrarAtualizacao(alocacaoNova, alocacaoAntiga, usuario, "Ajuste");

        verify(historicoAlteracaoRepository, times(2)).save(org.mockito.ArgumentMatchers.any(HistoricoAlteracao.class));
    }

    private Alocacao criarAlocacao(Long idAlocacao, Long idTurma, Long idDisciplina, Long idSala, Long idProfessor,
            Long idDiaSemana, Long idBlocoHorario, Long idGradeHoraria) {

        Turma turma = new Turma();
        turma.setId(idTurma);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(idDisciplina);

        Sala sala = new Sala();
        sala.setId(idSala);

        Professor professor = new Professor();
        professor.setId(idProfessor);

        DiaSemana diaSemana = new DiaSemana();
        diaSemana.setId(idDiaSemana);

        BlocoHorario blocoHorario = new BlocoHorario();
        blocoHorario.setId(idBlocoHorario);

        GradeHoraria gradeHoraria = new GradeHoraria();
        gradeHoraria.setCurso(new com.fatec.gini.domain.entities.Curso());
        gradeHoraria.getCurso().setId(70L);
        gradeHoraria.setPeriodoLetivo(new com.fatec.gini.domain.entities.PeriodoLetivo());
        gradeHoraria.getPeriodoLetivo().setId(80L);
        java.lang.reflect.Field idField;
        try {
            idField = GradeHoraria.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(gradeHoraria, idGradeHoraria);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Alocacao alocacao = new Alocacao();
        alocacao.setId(idAlocacao);
        alocacao.setTurma(turma);
        alocacao.setDisciplina(disciplina);
        alocacao.setSala(sala);
        alocacao.setProfessor(professor);
        alocacao.setDiaSemana(diaSemana);
        alocacao.setBlocoHorario(blocoHorario);
        alocacao.setGradeHoraria(gradeHoraria);

        return alocacao;
    }
}

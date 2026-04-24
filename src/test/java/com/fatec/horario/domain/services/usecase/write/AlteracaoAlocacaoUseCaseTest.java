package com.fatec.horario.domain.services.usecase.write;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import com.fatec.horario.domain.entities.Alocacao;
import com.fatec.horario.domain.entities.DiaSemana;
import com.fatec.horario.domain.entities.Disciplina;
import com.fatec.horario.domain.entities.GradeHoraria;
import com.fatec.horario.domain.entities.HistoricoAlteracao;
import com.fatec.horario.domain.entities.Horario;
import com.fatec.horario.domain.entities.Sala;
import com.fatec.horario.domain.entities.Turma;
import com.fatec.horario.domain.entities.Usuario;
import com.fatec.horario.infrastructure.repositories.HistoricoAlteracaoRepository;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlteracaoAlocacaoUseCaseTest {

    @Mock
    private HistoricoAlteracaoRepository historicoAlteracaoRepository;

    @InjectMocks
    private AlteracaoAlocacaoUseCase alteracaoAlocacaoUseCase;

    @Test
    void deveRegistrarHistoricoQuandoHouverAlteracao() {
        Alocacao alocacaoAtual = criarAlocacao(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L);
        Turma novaTurma = criarTurma(2L);
        Disciplina novaDisciplina = criarDisciplina(10L);
        Sala novaSala = criarSala(100L);
        Usuario novoUsuario = criarUsuario(1000L);
        Usuario usuarioAlteracao = criarUsuario(2000L);
        DiaSemana novoDiaSemana = criarDiaSemana(10000L);
        Horario novoHorario = criarHorario(100000L);
        GradeHoraria novaGradeHoraria = criarGradeHoraria(1000000L);

        when(historicoAlteracaoRepository.save(any(HistoricoAlteracao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        alteracaoAlocacaoUseCase.executarCasoUso(
                alocacaoAtual,
                novaTurma,
                novaDisciplina,
                novaSala,
                novoUsuario,
                usuarioAlteracao,
                novoDiaSemana,
                novoHorario,
                novaGradeHoraria,
                "Atualização necessária");

        ArgumentCaptor<HistoricoAlteracao> captor = ArgumentCaptor.forClass(HistoricoAlteracao.class);
        verify(historicoAlteracaoRepository, times(1)).save(captor.capture());

        HistoricoAlteracao historico = captor.getValue();
        assertEquals("turma", historico.getCampoAlterado());
        assertEquals("1", historico.getValorAntigo());
        assertEquals("2", historico.getValorNovo());
        assertEquals("Atualização necessária", historico.getJustificativa());
        assertEquals(alocacaoAtual, historico.getAlocacao());
        assertEquals(usuarioAlteracao, historico.getUsuario());
    }

    @Test
    void naoDeveRegistrarHistoricoQuandoNaoHouverAlteracao() {
        Alocacao alocacaoAtual = criarAlocacao(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L);

        assertDoesNotThrow(() -> alteracaoAlocacaoUseCase.executarCasoUso(
                alocacaoAtual,
                alocacaoAtual.getTurma(),
                alocacaoAtual.getDisciplina(),
                alocacaoAtual.getSala(),
                alocacaoAtual.getUsuario(),
                criarUsuario(2000L),
                alocacaoAtual.getDiaSemana(),
                alocacaoAtual.getHorario(),
                alocacaoAtual.getGradeHoraria(),
                "Sem mudanças"));

        verify(historicoAlteracaoRepository, never()).save(any(HistoricoAlteracao.class));
    }

    private Alocacao criarAlocacao(Long turmaId, Long disciplinaId, Long salaId, Long usuarioId,
            Long diaSemanaId, Long horarioId, Long gradeHorariaId) {
        Turma turma = criarTurma(turmaId);
        Disciplina disciplina = criarDisciplina(disciplinaId);
        Sala sala = criarSala(salaId);
        Usuario usuario = criarUsuario(usuarioId);
        DiaSemana diaSemana = criarDiaSemana(diaSemanaId);
        Horario horario = criarHorario(horarioId);
        GradeHoraria gradeHoraria = criarGradeHoraria(gradeHorariaId);

        return new Alocacao(turma, disciplina, sala, usuario, diaSemana, horario, gradeHoraria);
    }

    private Turma criarTurma(Long id) {
        Turma turma = new Turma();
        turma.setId(id);
        return turma;
    }

    private Disciplina criarDisciplina(Long id) {
        Disciplina disciplina = new Disciplina();
        ReflectionTestUtils.setField(disciplina, "id", id);
        return disciplina;
    }

    private Sala criarSala(Long id) {
        Sala sala = new Sala();
        sala.setId(id);
        return sala;
    }

    private Usuario criarUsuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        return usuario;
    }

    private DiaSemana criarDiaSemana(Long id) {
        DiaSemana diaSemana = new DiaSemana();
        diaSemana.setId(id);
        return diaSemana;
    }

    private Horario criarHorario(Long id) {
        Horario horario = new Horario();
        horario.setId(id);
        return horario;
    }

    private GradeHoraria criarGradeHoraria(Long id) {
        GradeHoraria gradeHoraria = new GradeHoraria();
        ReflectionTestUtils.setField(gradeHoraria, "id", id);
        return gradeHoraria;
    }
}

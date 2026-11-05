package com.fatec.gini.domain.services.usecase.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DiaSemana;
import com.fatec.gini.domain.entities.HistoricoVersaoAlocacao;
import com.fatec.gini.domain.entities.Professor;
import com.fatec.gini.domain.entities.QuadroHorario;
import com.fatec.gini.domain.entities.Sala;
import com.fatec.gini.domain.entities.Turma;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.infrastructure.repositories.HistoricoVersaoAlocacaoRepository;

@ExtendWith(MockitoExtension.class)
class RegistrarHistoricoVersaoAlocacaoUseCaseTest {

    @Mock
    private HistoricoVersaoAlocacaoRepository repository;

    @InjectMocks
    private RegistrarHistoricoVersaoAlocacaoUseCase useCase;

    @Test
    void deveRegistrarCriacaoComValorNovoEUsuarioResponsavel() {
        Alocacao alocacao = mock(Alocacao.class);
        Usuario usuario = mock(Usuario.class);
        when(alocacao.getId()).thenReturn(42L);

        useCase.registrarCriacao(alocacao, usuario);

        ArgumentCaptor<HistoricoVersaoAlocacao> captor = ArgumentCaptor.forClass(HistoricoVersaoAlocacao.class);
        verify(repository).save(captor.capture());
        HistoricoVersaoAlocacao historico = captor.getValue();

        assertThat(historico.getCampoAlterado()).isEqualTo("alocacao");
        assertThat(historico.getValorAntigo()).isNull();
        assertThat(historico.getValorNovo()).isEqualTo("42");
        assertThat(historico.getJustificativa()).isEqualTo("Criação de alocação");
        assertThat(historico.getDataAlteracao()).isNotNull();
        assertThat(historico.getAlocacao()).isSameAs(alocacao);
        assertThat(historico.getUsuario()).isSameAs(usuario);
    }

    @Test
    void deveRegistrarSomenteCamposAlteradosComValoresNaOrdemAntigoENovo() {
        Alocacao antiga = alocacao(10L, 20L, 30L, 40L, DiaSemana.SEGUNDA, 50L, 60L);
        Alocacao nova = alocacao(11L, 21L, 31L, 41L, DiaSemana.TERÇA, 51L, 61L);
        Usuario usuario = mock(Usuario.class);

        useCase.registrarAtualizacao(nova, antiga, usuario, "Ajuste de grade");

        ArgumentCaptor<HistoricoVersaoAlocacao> captor = ArgumentCaptor.forClass(HistoricoVersaoAlocacao.class);
        verify(repository, times(7)).save(captor.capture());
        Map<String, HistoricoVersaoAlocacao> porCampo = captor.getAllValues().stream()
                .collect(Collectors.toMap(HistoricoVersaoAlocacao::getCampoAlterado, Function.identity()));

        assertThat(porCampo).hasSize(7);
        porCampo.values().forEach(historico -> {
            assertThat(historico.getJustificativa()).isEqualTo("Ajuste de grade");
            assertThat(historico.getAlocacao()).isSameAs(nova);
            assertThat(historico.getUsuario()).isSameAs(usuario);
        });
        assertThat(porCampo.get("turma").getValorAntigo()).isEqualTo("10");
        assertThat(porCampo.get("turma").getValorNovo()).isEqualTo("11");
        assertThat(porCampo.get("diaSemana").getValorAntigo()).isEqualTo("SEGUNDA");
        assertThat(porCampo.get("diaSemana").getValorNovo()).isEqualTo("TERÇA");
        assertThat(porCampo.get("quadroHorario").getValorAntigo()).isEqualTo("60");
        assertThat(porCampo.get("quadroHorario").getValorNovo()).isEqualTo("61");
    }

    @Test
    void naoDevePersistirHistoricoQuandoAtualizacaoNaoMudarNenhumCampo() {
        Alocacao antiga = alocacao(10L, 20L, 30L, 40L, DiaSemana.SEGUNDA, 50L, 60L);
        Alocacao nova = alocacao(10L, 20L, 30L, 40L, DiaSemana.SEGUNDA, 50L, 60L);

        useCase.registrarAtualizacao(nova, antiga, mock(Usuario.class), "Sem alterações");

        verify(repository, times(0)).save(any(HistoricoVersaoAlocacao.class));
    }

    private Alocacao alocacao(Long turmaId, Long disciplinaId, Long salaId, Long professorId,
            DiaSemana diaSemana, Long blocoId, Long quadroId) {
        Alocacao alocacao = mock(Alocacao.class);
        Turma turma = mock(Turma.class);
        com.fatec.gini.domain.entities.Disciplina disciplina = mock(com.fatec.gini.domain.entities.Disciplina.class);
        Sala sala = mock(Sala.class);
        Professor professor = mock(Professor.class);
        BlocoHorario bloco = mock(BlocoHorario.class);
        QuadroHorario quadro = mock(QuadroHorario.class);

        when(turma.getId()).thenReturn(turmaId);
        when(disciplina.getId()).thenReturn(disciplinaId);
        when(sala.getId()).thenReturn(salaId);
        when(professor.getId()).thenReturn(professorId);
        when(bloco.getId()).thenReturn(blocoId);
        when(quadro.getId()).thenReturn(quadroId);
        when(alocacao.getTurma()).thenReturn(turma);
        when(alocacao.getDisciplina()).thenReturn(disciplina);
        when(alocacao.getSala()).thenReturn(sala);
        when(alocacao.getProfessor()).thenReturn(professor);
        when(alocacao.getDiaSemana()).thenReturn(diaSemana);
        when(alocacao.getBlocoHorario()).thenReturn(bloco);
        when(alocacao.getQuadroHorario()).thenReturn(quadro);
        return alocacao;
    }
}

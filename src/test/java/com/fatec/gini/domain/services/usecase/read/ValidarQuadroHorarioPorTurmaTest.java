package com.fatec.gini.domain.services.usecase.read;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.fatec.gini.domain.entities.*;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.infrastructure.repositories.QuadroHorarioRepository;
import com.fatec.gini.web.exception.BusinessException;

class ValidarQuadroHorarioPorTurmaTest {
    @Test
    void comparaUltimaVersaoSomenteDaMesmaTurmaEPeriodo() {
        var repo = mock(QuadroHorarioRepository.class);
        var turma = new Turma(); turma.setId  (10L);
        var periodo = new PeriodoAtividadeQuadro(); periodo.setId(20L);
        var quadro = new QuadroHorario(); quadro.setId(30L); quadro.setTurma(turma);
        quadro.setPeriodoAtividadeQuadro(periodo); quadro.setStatus(Status.ATIVO);
        var alocacao = new Alocacao(); alocacao.setQuadroHorario(quadro); alocacao.setTurma(turma);
        when(repo.findTopByTurmaIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(10L, 20L)).thenReturn(Optional.of(quadro));
        var validacao = new ValidarQuadroHorarioUseCase(repo);
        validacao.executar(alocacao);
        var outraVersao = new QuadroHorario(); outraVersao.setId(31L);
        when(repo.findTopByTurmaIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(10L, 20L)).thenReturn(Optional.of(outraVersao));
        assertThatThrownBy(() -> validacao.executar(alocacao)).isInstanceOf(BusinessException.class);
        verify(repo, never()).findTopByCursoIdAndPeriodoAtividadeQuadroIdOrderByVersaoDesc(any(), any());
    }
}

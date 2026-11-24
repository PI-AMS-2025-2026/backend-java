package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.BlocoHorario;
import com.fatec.gini.domain.entities.DisponibilidadeProfessor;
import com.fatec.gini.domain.services.usecase.read.ValidarBlocoHorarioUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.BlocoHorarioRepository;
import com.fatec.gini.infrastructure.repositories.DisponibilidadeProfessorRepository;
import com.fatec.gini.infrastructure.repositories.HistoricoVersaoAlocacaoRepository;
import com.fatec.gini.web.exception.BlocoHorarioComVinculosException;

@ExtendWith(MockitoExtension.class)
class BlocoHorarioServiceExclusaoTest {
    @Mock private BlocoHorarioRepository blocoRepository;
    @Mock private ValidarBlocoHorarioUseCase validarUseCase;
    @Mock private AlocacaoRepository alocacaoRepository;
    @Mock private DisponibilidadeProfessorRepository disponibilidadeRepository;
    @Mock private HistoricoVersaoAlocacaoRepository historicoRepository;

    private BlocoHorarioService service;
    private BlocoHorario bloco;

    @BeforeEach
    void setUp() {
        service = new BlocoHorarioService(blocoRepository, validarUseCase, alocacaoRepository,
                disponibilidadeRepository, historicoRepository);
        bloco = new BlocoHorario();
        when(blocoRepository.findById(4L)).thenReturn(Optional.of(bloco));
    }

    @Test
    void exigeConfirmacaoQuandoExistemAlocacoesEDisponibilidades() {
        when(alocacaoRepository.findByBlocoHorarioId(4L)).thenReturn(List.of(new Alocacao(), new Alocacao()));
        when(disponibilidadeRepository.findByBlocoHorarioId(4L)).thenReturn(List.of(new DisponibilidadeProfessor()));

        assertThatThrownBy(() -> service.deletar(4L, false))
                .isInstanceOf(BlocoHorarioComVinculosException.class)
                .hasMessageContaining("2 alocações")
                .hasMessageContaining("1 disponibilidades");
        verify(alocacaoRepository, never()).deleteAll(org.mockito.ArgumentMatchers.<Alocacao>anyList());
        verify(disponibilidadeRepository, never()).deleteAll(org.mockito.ArgumentMatchers.<DisponibilidadeProfessor>anyList());
        verify(blocoRepository, never()).delete(bloco);
    }

    @Test
    void confirmaExclusaoNaOrdemCorretaPreservandoHistorico() {
        Alocacao alocacao = new Alocacao();
        alocacao.setId(20L);
        var alocacoes = List.of(alocacao);
        var disponibilidades = List.of(new DisponibilidadeProfessor());
        when(alocacaoRepository.findByBlocoHorarioId(4L)).thenReturn(alocacoes);
        when(disponibilidadeRepository.findByBlocoHorarioId(4L)).thenReturn(disponibilidades);

        service.deletar(4L, true);

        InOrder order = inOrder(historicoRepository, alocacaoRepository, disponibilidadeRepository, blocoRepository);
        order.verify(historicoRepository).desvincularAlocacoes(List.of(20L));
        order.verify(alocacaoRepository).deleteAll(alocacoes);
        order.verify(disponibilidadeRepository).deleteAll(disponibilidades);
        order.verify(blocoRepository).delete(bloco);
    }
}

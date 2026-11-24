package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;

import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.HistoricoVersaoAlocacaoRepository;
import com.fatec.gini.infrastructure.repositories.RecursoSalaRepository;
import com.fatec.gini.infrastructure.repositories.SalaRepository;
import com.fatec.gini.infrastructure.repositories.TipoSalaRepository;
import com.fatec.gini.web.exception.SalaComAlocacoesException;

@ExtendWith(MockitoExtension.class)
class SalaServiceExclusaoTest {

    @Mock private SalaRepository salaRepository;
    @Mock private TipoSalaRepository tipoSalaRepository;
    @Mock private AlocacaoRepository alocacaoRepository;
    @Mock private HistoricoVersaoAlocacaoRepository historicoRepository;
    @Mock private RecursoSalaRepository recursoSalaRepository;

    private SalaService service;

    @BeforeEach
    void setUp() {
        service = new SalaService(salaRepository, tipoSalaRepository, alocacaoRepository,
                historicoRepository, recursoSalaRepository);
        when(salaRepository.existsById(4L)).thenReturn(true);
    }

    @Test
    void informaQuantidadeEExigeConfirmacaoQuandoSalaTemAlocacoes() {
        when(alocacaoRepository.findBySalaId(4L)).thenReturn(List.of(new Alocacao(), new Alocacao()));

        assertThatThrownBy(() -> service.deletar(4L, false))
                .isInstanceOf(SalaComAlocacoesException.class)
                .hasMessageContaining("2 alocação");

        verify(alocacaoRepository, never()).deleteAll(org.mockito.ArgumentMatchers.<Alocacao>anyList());
        verify(salaRepository, never()).deleteById(4L);
    }

    @Test
    void confirmaEExcluiAlocacoesPreservandoHistorico() {
        Alocacao uma = new Alocacao();
        uma.setId(20L);
        Alocacao duas = new Alocacao();
        duas.setId(21L);
        when(alocacaoRepository.findBySalaId(4L)).thenReturn(List.of(uma, duas));

        service.deletar(4L, true);

        verify(historicoRepository).desvincularAlocacoes(List.of(20L, 21L));
        verify(alocacaoRepository).deleteAll(List.of(uma, duas));
        verify(recursoSalaRepository).deleteAllBySalaId(4L);
        verify(salaRepository).deleteById(4L);
    }
}

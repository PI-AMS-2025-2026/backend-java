package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import com.fatec.gini.domain.entities.HistoricoVersaoAlocacao;
import com.fatec.gini.domain.services.usecase.read.ValidarAutorizacaoCursoUseCase;
import com.fatec.gini.dto.historicoVersaoAlocacao.HistoricoVersaoAlocacaoResponse;
import com.fatec.gini.infrastructure.repositories.HistoricoVersaoAlocacaoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class HistoricoVersaoAlocacaoServiceTest {

    @Mock
    private HistoricoVersaoAlocacaoRepository repository;

    @Mock
    private ValidarAutorizacaoCursoUseCase validarAutorizacaoCurso;

    @InjectMocks
    private HistoricoVersaoAlocacaoService service;

    @Test
    void deveListarHistoricoAplicandoFiltrosEMapeandoPaginacao() {
        HistoricoVersaoAlocacao historico = new HistoricoVersaoAlocacao(
                LocalDate.of(2026, 10, 29), "sala", "1", "2");
        when(validarAutorizacaoCurso.cursoParaFiltro(null)).thenReturn(7L);
        when(repository.buscarPorFiltros(42L, 9L, 7L, org.springframework.data.domain.PageRequest.of(1, 5)))
                .thenReturn(new PageImpl<>(List.of(historico), org.springframework.data.domain.PageRequest.of(1, 5), 6));

        var resultado = service.listar(42L, 9L, 1, 5);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.page()).isEqualTo(1);
        assertThat(resultado.size()).isEqualTo(5);
        assertThat(resultado.totalElements()).isEqualTo(6);
        assertThat(resultado.totalPages()).isEqualTo(2);
        HistoricoVersaoAlocacaoResponse response = resultado.content().get(0);
        assertThat(response.campoAlterado()).isEqualTo("sala");
        assertThat(response.valorAntigo()).isEqualTo("1");
        assertThat(response.valorNovo()).isEqualTo("2");
        verify(validarAutorizacaoCurso).cursoParaFiltro(null);
        verify(repository).buscarPorFiltros(42L, 9L, 7L, org.springframework.data.domain.PageRequest.of(1, 5));
    }

    @Test
    void deveInformarQuandoHistoricoNaoExistir() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}

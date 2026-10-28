package com.fatec.gini.domain.services.usecase.write;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Alocacao;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;
import com.fatec.gini.domain.services.usecase.read.ValidarAlocacaoUseCase;
import com.fatec.gini.domain.services.usecase.read.ValidarReferenciasObrigatoriasAlocacaoUseCase;
import com.fatec.gini.infrastructure.repositories.AlocacaoRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class CriarAlocacaoUseCaseTest {

    @Mock
    private ValidarAlocacaoUseCase validarAlocacaoUseCase;

    @Mock
    private ValidarReferenciasObrigatoriasAlocacaoUseCase validarReferencias;

    @Mock
    private RegistrarHistoricoVersaoAlocacaoUseCase historico;

    @Mock
    private AlocacaoRepository repository;

    @InjectMocks
    private CriarAlocacaoUseCase useCase;

    @Test
    void deveValidarPersistirERegistrarHistoricoNaCriacao() {
        Alocacao alocacao = new Alocacao();
        Usuario usuario = new Usuario("Admin", "admin@gini.local", "senha", Status.ATIVO,
                TipoUsuario.ADMINISTRADOR);
        when(validarReferencias.buscarUsuarioAlteracaoPorId(1L)).thenReturn(usuario);
        when(repository.save(alocacao)).thenReturn(alocacao);

        Alocacao resultado = useCase.executar(alocacao, 1L);

        assertThat(resultado).isSameAs(alocacao);
        verify(validarAlocacaoUseCase).executar(alocacao);
        verify(repository).save(alocacao);
        verify(historico).registrarCriacao(alocacao, usuario);
    }

    @Test
    void naoDevePersistirQuandoRegraDeAlocacaoFalhar() {
        Alocacao alocacao = new Alocacao();
        Usuario usuario = new Usuario("Admin", "admin@gini.local", "senha", Status.ATIVO,
                TipoUsuario.ADMINISTRADOR);
        when(validarReferencias.buscarUsuarioAlteracaoPorId(1L)).thenReturn(usuario);
        org.mockito.Mockito.doThrow(new BusinessException("Conflito de horário"))
                .when(validarAlocacaoUseCase).executar(alocacao);

        org.junit.jupiter.api.Assertions.assertThrows(
                BusinessException.class,
                () -> useCase.executar(alocacao, 1L));

        verify(repository, never()).save(any(Alocacao.class));
        verify(historico, never()).registrarCriacao(any(Alocacao.class), any(Usuario.class));
    }
}
package com.fatec.gini.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;
import com.fatec.gini.domain.services.usecase.read.ValidarCursoObrigatorioUsuarioUseCase;
import com.fatec.gini.dto.id.LongDTO;
import com.fatec.gini.dto.usuario.UsuarioRequest;
import com.fatec.gini.dto.usuario.UsuarioResponse;
import com.fatec.gini.infrastructure.repositories.CursoRepository;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;
import com.fatec.gini.web.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private ValidarCursoObrigatorioUsuarioUseCase validarCursoObrigatorioUsuario;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void devePermitirAdministradorSemCurso() {
        UsuarioRequest request = request(TipoUsuario.ADMINISTRADOR, null);
        Usuario usuario = usuario(request);
        usuario.setId(1L);

        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse response = usuarioService.criar(request);

        assertThat(response.tipoUsuario()).isEqualTo(TipoUsuario.ADMINISTRADOR);
        assertThat(response.curso()).isNull();
    }

    @Test
    void deveRejeitarCoordenadorSemCurso() {
        UsuarioRequest request = request(TipoUsuario.COORDENADOR, null);
        doThrow(new BusinessException("Coordenador deve estar associado a um curso"))
            .when(validarCursoObrigatorioUsuario).executar(request);

        assertThrows(BusinessException.class, () -> usuarioService.criar(request));
    }

    @Test
    void devePermitirCoordenadorComCursoExistente() {
        Curso curso = new Curso("ADS", "Semestral", Status.ATIVO, 6);
        curso.setId(7L);
        UsuarioRequest request = request(TipoUsuario.COORDENADOR, new LongDTO(7L));
        Usuario usuario = usuario(request);
        usuario.setId(2L);
        usuario.setCurso(curso);

        when(cursoRepository.findById(7L)).thenReturn(Optional.of(curso));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse response = usuarioService.criar(request);

        assertThat(response.tipoUsuario()).isEqualTo(TipoUsuario.COORDENADOR);
        assertThat(response.curso().id()).isEqualTo(7L);
    }

    @Test
    void deveAtualizarUsuarioSemAlterarSenhaQuandoSenhaNaoInformada() {
        Usuario existente = new Usuario("Nome antigo", "admin@teste.com", "hash-da-senha", Status.ATIVO,
                TipoUsuario.ADMINISTRADOR);
        existente.setId(4L);
        UsuarioRequest request = new UsuarioRequest(
                "Nome novo", "admin@teste.com", null, Status.ATIVO, TipoUsuario.ADMINISTRADOR, null);

        when(repository.findById(4L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        UsuarioResponse response = usuarioService.atualizar(4L, request);

        assertThat(response.nome()).isEqualTo("Nome novo");
        assertThat(existente.getSenha()).isEqualTo("hash-da-senha");
    }

    @Test
    void deveRejeitarCriacaoSemSenha() {
        UsuarioRequest request = new UsuarioRequest(
                "Usuário Teste", "novo@teste.com", null, Status.ATIVO, TipoUsuario.ADMINISTRADOR, null);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.criar(request));
    }

    private UsuarioRequest request(TipoUsuario tipoUsuario, LongDTO curso) {
        return new UsuarioRequest(
                "Usuário Teste",
                tipoUsuario.name().toLowerCase() + "@teste.com",
                "123456",
                Status.ATIVO,
                tipoUsuario,
                curso);
    }

    private Usuario usuario(UsuarioRequest request) {
        return new Usuario(
                request.nome(),
                request.email(),
                request.senha(),
                request.status(),
                request.tipoUsuario());
    }
}

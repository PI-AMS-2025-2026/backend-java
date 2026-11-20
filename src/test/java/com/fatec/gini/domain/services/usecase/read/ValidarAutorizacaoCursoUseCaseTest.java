package com.fatec.gini.domain.services.usecase.read;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.fatec.gini.domain.entities.Curso;
import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.TipoUsuario;

import org.springframework.security.access.AccessDeniedException;

class ValidarAutorizacaoCursoUseCaseTest {

    private final ValidarAutorizacaoCursoUseCase useCase = new ValidarAutorizacaoCursoUseCase();

    @BeforeEach
    void habilitarAutorizacao() {
        ReflectionTestUtils.setField(useCase, "authorizationEnabled", true);
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void coordenadorPodeAcessarSomenteOProprioCurso() {
        Curso cursoDoCoordenador = curso(7L);
        Usuario coordenador = usuario(TipoUsuario.COORDENADOR, cursoDoCoordenador);
        autenticar(coordenador);

        useCase.validarCurso(cursoDoCoordenador);

        assertThat(useCase.cursoParaFiltro(99L)).isEqualTo(7L);
        assertThatThrownBy(() -> useCase.validarCurso(curso(99L)))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("acesso a este curso");
    }

    @Test
    void administradorPodeAcessarQualquerCursoEManterFiltroSolicitado() {
        Usuario administrador = usuario(TipoUsuario.ADMINISTRADOR, null);
        autenticar(administrador);

        useCase.validarCurso(curso(7L));

        assertThat(useCase.cursoParaFiltro(99L)).isEqualTo(99L);
    }

    private Curso curso(Long id) {
        Curso curso = mock(Curso.class);
        when(curso.getId()).thenReturn(id);
        return curso;
    }

    private Usuario usuario(TipoUsuario tipo, Curso curso) {
        Usuario usuario = new Usuario();
        usuario.setTipoUsuario(tipo);
        usuario.setCurso(curso);
        return usuario;
    }

    private void autenticar(Usuario usuario) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }
}

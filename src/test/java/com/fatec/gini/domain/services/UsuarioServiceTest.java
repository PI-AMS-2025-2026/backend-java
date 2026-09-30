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

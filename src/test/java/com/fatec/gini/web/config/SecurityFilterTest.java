package com.fatec.gini.web.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fatec.gini.domain.entities.Usuario;
import com.fatec.gini.domain.models.Status;
import com.fatec.gini.domain.models.TipoUsuario;
import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;
import com.fatec.gini.infrastructure.repositories.RefreshTokenRepository;

import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityFilter securityFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve continuar a corrente de filtros sem autenticar quando não houver cabeçalho Authorization")
    void doFilterInternal_semCabecalhoAuthorization_naoDeveAutenticar() throws Exception {
        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenService, never()).validarToken(anyString());
        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve continuar sem autenticar quando o cabeçalho não iniciar com 'Bearer '")
    void doFilterInternal_cabecalhoSemBearer_naoDeveAutenticar() throws Exception {
        request.addHeader("Authorization", "Basic dXNlcjpzZW5oYQ==");

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenService, never()).validarToken(anyString());
        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve buscar no repositório quando o token for inválido (retornar vazio ou nulo)")
    void doFilterInternal_tokenInvalido_naoDeveBuscarUsuario() throws Exception {
        request.addHeader("Authorization", "Bearer token_invalido");
        when(tokenService.validarToken("token_invalido")).thenReturn("");

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenService).validarToken("token_invalido");
        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar quando o usuário não for encontrado no repositório")
    void doFilterInternal_usuarioNaoEncontrado_naoDeveAutenticar() throws Exception {
        String email = "inexistente@fatec.sp.gov.br";
        request.addHeader("Authorization", "Bearer valid_token");
        when(tokenService.validarToken("valid_token")).thenReturn(email);
        when(tokenService.validarSessao("valid_token")).thenReturn("session");
        when(refreshTokenRepository.findByToken("session")).thenReturn(java.util.Optional.of(mock(com.fatec.gini.domain.entities.RefreshToken.class)));
        when(usuarioRepository.findByEmail(email)).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenService).validarToken("valid_token");
        verify(usuarioRepository).findByEmail(email);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar quando o usuário estiver inativo (Status.INATIVO)")
    void doFilterInternal_usuarioInativo_naoDeveAutenticar() throws Exception {
        String email = "inativo@fatec.sp.gov.br";
        request.addHeader("Authorization", "Bearer valid_token");
        Usuario usuarioInativo = new Usuario("Usuario Inativo", email, "123456", Status.INATIVO, TipoUsuario.COORDENADOR);

        when(tokenService.validarToken("valid_token")).thenReturn(email);
        when(tokenService.validarSessao("valid_token")).thenReturn("session");
        when(refreshTokenRepository.findByToken("session")).thenReturn(java.util.Optional.of(mock(com.fatec.gini.domain.entities.RefreshToken.class)));
        when(usuarioRepository.findByEmail(email)).thenReturn(usuarioInativo);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenService).validarToken("valid_token");
        verify(usuarioRepository).findByEmail(email);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve autenticar com sucesso quando o token for válido e o usuário estiver ativo")
    void doFilterInternal_tokenEUsuarioValidos_deveAutenticarComSucesso() throws Exception {
        String email = "admin@fatec.sp.gov.br";
        request.addHeader("Authorization", "Bearer valid_jwt_token");
        Usuario usuarioAtivo = new Usuario("Admin Teste", email, "123456", Status.ATIVO, TipoUsuario.ADMINISTRADOR);

        when(tokenService.validarToken("valid_jwt_token")).thenReturn(email);
        when(tokenService.validarSessao("valid_jwt_token")).thenReturn("session");
        when(refreshTokenRepository.findByToken("session")).thenReturn(java.util.Optional.of(mock(com.fatec.gini.domain.entities.RefreshToken.class)));
        when(usuarioRepository.findByEmail(email)).thenReturn(usuarioAtivo);

        securityFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(usuarioAtivo, authentication.getPrincipal());
        assertNotNull(authentication.getDetails());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve extrair corretamente o token contendo 'Bearer ' dentro do valor do token")
    void doFilterInternal_tokenComBearerInterno_deveExtrairCorretamente() throws Exception {
        String tokenComPalavraBearer = "xyz_Bearer_abc";
        request.addHeader("Authorization", "Bearer " + tokenComPalavraBearer);
        when(tokenService.validarToken(tokenComPalavraBearer)).thenReturn("");

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(tokenService).validarToken(tokenComPalavraBearer);
    }
}

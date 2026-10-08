package com.fatec.gini.web.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fatec.gini.domain.services.TokenService;
import com.fatec.gini.infrastructure.repositories.UsuarioRepository;
import com.fatec.gini.infrastructure.repositories.RefreshTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Recupera e valida o Bearer token antes de encaminhar a requisicao.
        var token = this.recoverToken(request);

        if (token != null) {
            var email = tokenService.validarToken(token);
                var session = tokenService.validarSessao(token);
                    if (email != null && !email.isBlank()
                        && session != null
                        && refreshTokenRepository.findByToken(session).isPresent()) {
                UserDetails usuario = usuarioRepository.findByEmail(email);

                if (usuario != null && usuario.isEnabled()) {
                    // Coloca o usuario autenticado no contexto usado pelos controllers.
                    var auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        // Aceita somente o formato Authorization: Bearer <token>.
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
            return null;
        }
        return authHeader.substring(7).trim();
    }
}


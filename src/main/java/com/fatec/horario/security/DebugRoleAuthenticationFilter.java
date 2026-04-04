package com.fatec.horario.security;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DebugRoleAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugRoleAuthenticationFilter.class);
    private static final String DEBUG_ROLE_HEADER = "X-Debug-Role";

    private static final Set<String> ALLOWED_ROLES = Set.of(
            SecurityRoles.ADMIN,
            SecurityRoles.COORDENADOR,
            SecurityRoles.PROFESSOR);

    @Value("${app.security.debug-role-enabled:false}")
    private boolean debugRoleEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!debugRoleEnabled || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String rawRole = request.getHeader(DEBUG_ROLE_HEADER);
        if (rawRole == null || rawRole.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String role = rawRole.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_ROLES.contains(role)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "X-Debug-Role invalido");
            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                "debug:" + role,
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        LOGGER.warn("Autenticacao de depuracao aplicada para role {} em {} {}", role, request.getMethod(),
                request.getRequestURI());

        filterChain.doFilter(request, response);
    }
}

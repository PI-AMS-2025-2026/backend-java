package com.fatec.horario.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fatec.horario.security.DebugRoleAuthenticationFilter;
import com.fatec.horario.security.JwtAuthenticationFilter;
import com.fatec.horario.security.SecurityRoles;

@Configuration
@EnableMethodSecurity
public class ConfiguracaoSeguranca {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final DebugRoleAuthenticationFilter debugRoleAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public ConfiguracaoSeguranca(
        JwtAuthenticationFilter jwtAuthenticationFilter,
        DebugRoleAuthenticationFilter debugRoleAuthenticationFilter,
        AuthenticationEntryPoint authenticationEntryPoint,
        AccessDeniedHandler accessDeniedHandler) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.debugRoleAuthenticationFilter = debugRoleAuthenticationFilter;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/auth/login",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/h2-console/**")
            .permitAll()

            .requestMatchers("/usuarios/**", "/tipo-usuario/**")
            .hasRole(SecurityRoles.ADMIN)

            .requestMatchers(HttpMethod.GET,
                "/curso/**",
                "/dias-semana/**",
                "/horarios/**",
                "/periodos-letivos/**",
                "/recursos/**",
                "/tipos-sala/**")
            .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.COORDENADOR, SecurityRoles.PROFESSOR)

            // Ponto de ajuste futuro: quando a matriz oficial de recursos autorizados for
            // consolidada, restringir aqui os endpoints de escrita/leitura por recurso para
            // COORDENADOR e PROFESSOR com granularidade fina.
            .requestMatchers(HttpMethod.POST,
                "/curso/**",
                "/dias-semana/**",
                "/horarios/**",
                "/periodos-letivos/**",
                "/recursos/**",
                "/tipos-sala/**")
            .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.COORDENADOR)
            .requestMatchers(HttpMethod.PUT,
                "/curso/**",
                "/dias-semana/**",
                "/horarios/**",
                "/periodos-letivos/**",
                "/recursos/**",
                "/tipos-sala/**")
            .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.COORDENADOR)
            .requestMatchers(HttpMethod.DELETE,
                "/curso/**",
                "/dias-semana/**",
                "/horarios/**",
                "/periodos-letivos/**",
                "/recursos/**",
                "/tipos-sala/**")
            .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.COORDENADOR)
            .anyRequest().authenticated())
        .httpBasic(httpBasic -> httpBasic.disable())
        .formLogin(form -> form.disable())
                .addFilterBefore(debugRoleAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, DebugRoleAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
    }
}
package com.fatec.gini.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@Profile("test")
@RequiredArgsConstructor
public class ConfiguracaoSegurancaTest {

    final SecurityFilter securityFilter;

    /**
     * Configurações de CORS para permitir requisições do frontend.
     * 
     * @return
     * @return
     */
    @Bean
    WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // CORS liberado para qualquer origem/IP em todos os ambientes.
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

        };
    }

    /**
     * Configurações de segurança para a aplicação.
     * 
     * @param http
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(
                        (request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/api/auth/login")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/auth/logout")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/auth/solicitar-recuperacao",
                                "/api/auth/redefinir-senha")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/grades/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/motor-quadro/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/api/historicos-versoes-alocacoes/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        // Libera a interface, os arquivos estaticos e a especificacao do Swagger.
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()

                        // Libera todas as operacoes do console H2 no perfil de teste.
                        .requestMatchers("/h2-console/**")
                        .permitAll()

                        // Somente administrador
                        .requestMatchers("/api/usuarios/**")
                        .hasRole("ADMIN")

                        // Admin e Coordenador
                        .requestMatchers("/api/disciplinas/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/api/turmas/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/api/alocacoes/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers(HttpMethod.GET, "/api/professores/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/api/professores/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/disponibilidades/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/api/periodos-atividade-quadro/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers(HttpMethod.GET, "/api/cursos/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/api/cursos/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/salas/**", "/api/tipos-salas/**",
                                "/api/recursos/**", "/api/tipos-recursos/**", "/api/recursos-salas/**",
                                "/api/bloco-horarios/**", "/api/disponibilidades-professores/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/salas/**", "/api/tipos-salas/**",
                                "/api/recursos/**", "/api/tipos-recursos/**", "/api/recursos-salas/**",
                                "/api/bloco-horarios/**", "/api/disponibilidades-professores/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/salas/**", "/api/tipos-salas/**",
                                "/api/recursos/**", "/api/tipos-recursos/**", "/api/recursos-salas/**",
                                "/api/bloco-horarios/**", "/api/disponibilidades-professores/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated())

                .addFilterBefore(
                        securityFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Bean para codificação de senhas usando BCrypt.
     * 
     * @return
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

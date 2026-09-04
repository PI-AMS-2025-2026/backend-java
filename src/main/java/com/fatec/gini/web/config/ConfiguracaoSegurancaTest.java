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
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("*");
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
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/auth/login")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/refresh")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/logout")
                        .permitAll()
                        
                        // Libera a interface, os arquivos estaticos e a especificacao do Swagger.
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()

                        // Libera todas as operacoes do console H2 no perfil de teste.
                        .requestMatchers("/h2-console/**")
                        .permitAll()

                        // .requestMatchers(HttpMethod.POST, "/auth/registrar")
                        // .permitAll()

                        // Somente administrador
                        .requestMatchers("/usuarios/**")
                        .hasRole("ADMIN")

                        // Admin e Coordenador
                        .requestMatchers("/disciplinas/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/turmas/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/grades/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/alocacoes/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/professores/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/disponibilidades/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/cursos/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

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
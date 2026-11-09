package com.fatec.gini.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@Profile("prod")
@RequiredArgsConstructor
public class ConfiguracaoSeguranca {

    final SecurityFilter securityFilter;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Rotas servidas pelo springdoc (Swagger UI e especificação OpenAPI).
     */
    static final String[] ROTAS_DOCUMENTACAO = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml"
    };

    /**
     * Cadeia de segurança exclusiva das rotas de documentação.
     *
     * Só existe quando app.swagger.enabled=true (default: false). Nos ambientes
     * em que a documentação está desligada este bean não é criado, as rotas caem
     * na cadeia principal e exigem autenticação — além de o próprio springdoc
     * estar desabilitado, resultando em 404.
     *
     * Quando ativa, as rotas ficam liberadas na aplicação: a autenticação é
     * delegada ao proxy reverso (Basic Auth, allowlist de IP ou VPN), conforme
     * configurado no repositório de infraestrutura. Por isso o serviço não deve
     * ser publicado diretamente na internet com esta opção ligada.
     *
     * @param http
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    @Order(1)
    @ConditionalOnProperty(name = "app.swagger.enabled", havingValue = "true")
    SecurityFilterChain documentacaoFilterChain(HttpSecurity http) throws Exception {

        return http
                .securityMatcher(ROTAS_DOCUMENTACAO)
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

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
                        .allowedOrigins(frontendUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
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
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Healthcheck do contêiner/orquestrador: precisa responder
                        // sem autenticação. Somente o health é exposto pelo Actuator
                        // (ver management.endpoints.web.exposure.include em
                        // application-prod.yml).
                        .requestMatchers("/actuator/health/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/login")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/refresh")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/logout")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/solicitar-recuperacao", "/auth/redefinir-senha")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/grades/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/motor-quadro/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/historicos-versoes-alocacoes/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        // Somente administrador
                        .requestMatchers("/usuarios/**")
                        .hasRole("ADMIN")

                        // Admin e Coordenador
                        .requestMatchers("/disciplinas/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/turmas/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/alocacoes/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers(HttpMethod.GET, "/professores/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/professores/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/disponibilidades/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers(HttpMethod.GET, "/cursos/**")
                        .hasAnyRole("ADMIN", "COORDENADOR")

                        .requestMatchers("/cursos/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/salas/**", "/tipos-salas/**", "/recursos/**",
                                "/tipos-recursos/**", "/recursos-salas/**", "/bloco-horarios/**",
                                "/disponibilidades-professores/**", "/periodos-atividade-quadro/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/salas/**", "/tipos-salas/**", "/recursos/**",
                                "/tipos-recursos/**", "/recursos-salas/**", "/bloco-horarios/**",
                                "/disponibilidades-professores/**", "/periodos-atividade-quadro/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/salas/**", "/tipos-salas/**", "/recursos/**",
                                "/tipos-recursos/**", "/recursos-salas/**", "/bloco-horarios/**",
                                "/disponibilidades-professores/**", "/periodos-atividade-quadro/**")
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

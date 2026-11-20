package com.fatec.gini.domain.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fatec.gini.domain.entities.Usuario;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expirationMs:86400000}")
    private Long tokenExpirationMs;

    /**
     * Emissor dos tokens, derivado do ambiente (ver api.security.token.issuer em
     * application.properties).
     *
     * Como o emissor também é validado na verificação, um token emitido em um
     * ambiente é recusado nos demais — mesmo que compartilhem o mesmo segredo.
     * Isso evita que um token obtido no ambiente de desenvolvimento (onde a
     * documentação da API fica acessível) seja reaproveitado em produção.
     */
    @Value("${api.security.token.issuer:fatec-gini}")
    private String issuer;

    // Cria o JWT usado para autorizar as requisicoes do usuario autenticado.
    public String gerarToken(Usuario usuario) {
        return gerarToken(usuario, null);
    }

    public String gerarToken(Usuario usuario, String sessionToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            var builder = JWT.create()
                    .withIssuer(issuer)
                    .withSubject(usuario.getEmail())
                    .withExpiresAt(gerarValidade());
            if (sessionToken != null) {
                builder.withClaim("session", sessionToken);
            }
            return builder.sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar um novo token");
        }
    }

    // Valida assinatura, emissor e validade do JWT e devolve o email do usuario.
    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject()

            ;

        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    public String validarSessao(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getClaim("session")
                    .asString();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    // Define a data de expiracao do token a partir da configuracao da aplicacao.
    private Instant gerarValidade() {
        return Instant.now().plusMillis(tokenExpirationMs);
    }
}

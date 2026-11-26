package com.fatec.gini.web.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Validações de configuração executadas na inicialização da aplicação.
 *
 * Só atua no perfil "prod", usado pelos ambientes hospedados. Os perfis dev e
 * test propositalmente usam um segredo fraco e padrão para facilitar a execução
 * local, e não devem ser bloqueados por estas regras.
 *
 * Qualquer violação impede a subida da aplicação: é preferível falhar no deploy,
 * de forma visível, a subir um ambiente mal configurado.
 */
@Component
@Profile("prod")
@Slf4j
public class ValidacaoConfiguracaoSeguranca {

    /**
     * HMAC256 usa chave de 256 bits; 32 caracteres é o mínimo coerente.
     * Gere um valor adequado com: openssl rand -hex 32
     */
    static final int TAMANHO_MINIMO_SEGREDO = 32;

    /**
     * Valores de exemplo, de teste ou herdados de documentação. Nenhum deles
     * pode ser usado em ambiente hospedado, independentemente do tamanho.
     */
    static final Set<String> SEGREDOS_PROIBIDOS = Set.of(
            "gini",
            "change_me",
            "changeme",
            "dummy",
            "secret",
            "senha",
            "password",
            "test",
            "teste",
            "mudar",
            "trocar",
            "exemplo",
            "example");

    /**
     * Ambientes em que a documentação da API nunca deve ser publicada.
     */
    static final Set<String> AMBIENTES_SEM_DOCUMENTACAO = Set.of(
            "prod",
            "production",
            "producao",
            "produção");

    private final String ambiente;
    private final String segredoJwt;
    private final boolean documentacaoHabilitada;

    public ValidacaoConfiguracaoSeguranca(
            @Value("${app.environment}") String ambiente,
            @Value("${api.security.token.secret}") String segredoJwt,
            @Value("${app.swagger.enabled:false}") boolean documentacaoHabilitada) {

        this.ambiente = ambiente;
        this.segredoJwt = segredoJwt;
        this.documentacaoHabilitada = documentacaoHabilitada;
    }

    @PostConstruct
    void validar() {

        validarSegredoJwt();
        validarDocumentacao();

        // Registrado para que a configuração efetiva fique visível no log de
        // inicialização, facilitando identificar um ambiente mal rotulado.
        log.info("Ambiente '{}' | documentação da API: {}",
                this.ambiente,
                this.documentacaoHabilitada ? "HABILITADA" : "desabilitada");
    }

    /**
     * Rejeita segredo ausente, curto ou reconhecidamente de exemplo.
     *
     * Não é possível verificar daqui se o segredo difere do de outro ambiente —
     * uma instância não conhece a configuração das demais. Esse isolamento é
     * obtido pelo emissor por ambiente (ver TokenService) somado a segredos
     * distintos por ambiente no gerenciador de secrets.
     */
    private void validarSegredoJwt() {

        if (this.segredoJwt == null || this.segredoJwt.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET não pode ser vazio. Gere um valor com: openssl rand -hex 32");
        }

        String normalizado = this.segredoJwt.trim().toLowerCase();

        if (SEGREDOS_PROIBIDOS.contains(normalizado)) {
            throw new IllegalStateException(
                    "JWT_SECRET está usando um valor de exemplo/teste, inaceitável em ambiente hospedado. "
                            + "Gere um valor com: openssl rand -hex 32");
        }

        if (this.segredoJwt.length() < TAMANHO_MINIMO_SEGREDO) {
            throw new IllegalStateException(
                    "JWT_SECRET é curto demais: tem %d caracteres e o mínimo é %d. "
                            .formatted(this.segredoJwt.length(), TAMANHO_MINIMO_SEGREDO)
                            + "Gere um valor com: openssl rand -hex 32");
        }
    }

    /**
     * Impede que a documentação da API seja publicada em produção.
     *
     * Protege contra erro de configuração: ligar SWAGGER_ENABLED no ambiente
     * errado passa a ser uma falha alta no deploy, e não uma exposição
     * silenciosa de /v3/api-docs.
     */
    private void validarDocumentacao() {

        if (!this.documentacaoHabilitada) {
            return;
        }

        String normalizado = this.ambiente == null ? "" : this.ambiente.trim().toLowerCase();

        if (AMBIENTES_SEM_DOCUMENTACAO.contains(normalizado)) {
            throw new IllegalStateException(
                    ("A documentação da API (SWAGGER_ENABLED=true) não pode ser publicada no ambiente '%s'. "
                            + "Habilite-a apenas no ambiente de desenvolvimento, e sempre com autenticação "
                            + "no proxy reverso.").formatted(this.ambiente));
        }
    }
}

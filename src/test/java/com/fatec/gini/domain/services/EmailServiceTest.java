package com.fatec.gini.domain.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import jakarta.mail.Session;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void deveEnviarEmailHtmlComFallbackEmTextoElinkDeRecuperacao() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        EmailService service = new EmailService(mailSender, templateEngine);
        ReflectionTestUtils.setField(service, "remetente", "noreply@gini.local");
        ReflectionTestUtils.setField(service, "frontendUrl", "http://localhost:3000/");
        ReflectionTestUtils.setField(service, "expiracaoMinutos", 15L);

        service.enviarRecuperacaoSenha("usuario@gini.local", "token-teste");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mimeMessage.writeTo(output);
        String email = output.toString(java.nio.charset.StandardCharsets.UTF_8);
        String corpo = extrairTexto(mimeMessage);

        assertTrue(email.contains("text/plain"));
        assertTrue(email.contains("text/html"));
        assertTrue(corpo.contains("http://localhost:3000/redefinir-senha?token=token-teste"));
        assertTrue(corpo.contains("Redefinir minha senha"));
        assertTrue(corpo.contains("15 minutos"));
        verify(mailSender).send(mimeMessage);
    }

    private String extrairTexto(Part parte) throws Exception {
        Object conteudo = parte.getContent();
        if (conteudo instanceof Multipart multipart) {
            StringBuilder texto = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                texto.append(extrairTexto(multipart.getBodyPart(i)));
            }
            return texto.toString();
        }
        return conteudo instanceof String ? (String) conteudo : "";
    }
}

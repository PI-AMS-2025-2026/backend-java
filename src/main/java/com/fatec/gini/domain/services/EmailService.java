package com.fatec.gini.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;

import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from:noreply@gini.local}")
    private String remetente;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.password-reset.expiration-minutes:15}")
    private long expiracaoMinutos;

    public void enviarRecuperacaoSenha(String destinatario, String token) {
        try {
            String linkRecuperacao = construirLinkRecuperacao(token);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject("Recuperação de senha | GINI");
            helper.setText(criarTextoAlternativo(linkRecuperacao), renderizarTemplate(linkRecuperacao));

            log.info("Tentando enviar e-mail de recuperação de senha para: {}", destinatario);
            mailSender.send(message);
            log.info("E-mail de recuperação enviado com sucesso para: {}", destinatario);
        } catch (Exception ex) {
            log.error("Falha ao enviar e-mail para {}: {}", destinatario, ex.getMessage(), ex);
            throw new BusinessException("Falha ao enviar e-mail: " + ex.getMessage());
        }
    }

    private String construirLinkRecuperacao(String token) {
        String urlBase = frontendUrl.replaceAll("/+$", "");
        return urlBase + "/redefinir-senha?token=" + token;
    }

    private String criarTextoAlternativo(String linkRecuperacao) {
        return "Olá!\n\n"
                + "Recebemos uma solicitação para redefinir a senha da sua conta GINI.\n"
                + "O link é válido por " + expiracaoMinutos + " minutos e só pode ser usado uma vez.\n\n"
                + "Acesse: " + linkRecuperacao + "\n\n"
                + "Se você não solicitou esta alteração, ignore este e-mail.\n\n"
                + "Equipe GINI";
    }

    private String renderizarTemplate(String linkRecuperacao) {
        Context contexto = new Context();
        contexto.setVariable("linkRecuperacao", linkRecuperacao);
        contexto.setVariable("expiracaoMinutos", expiracaoMinutos);
        return templateEngine.process("email/recuperacao-senha", contexto);
    }
}

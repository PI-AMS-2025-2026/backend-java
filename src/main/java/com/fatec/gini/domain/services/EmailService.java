package com.fatec.gini.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fatec.gini.web.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@gini.local}")
    private String remetente;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public void enviarRecuperacaoSenha(String destinatario, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(destinatario);
            message.setSubject("Recuperação de senha - GINI");
            message.setText("Olá!\n\n"
                    + "Use o link abaixo para redefinir sua senha. Ele expira em poucos minutos e só pode ser usado uma vez.\n\n"
                    + frontendUrl + "/redefinir-senha?token=" + token + "\n\n"
                    + "Se você não solicitou essa alteração, ignore este e-mail.");

            log.info("Tentando enviar e-mail de recuperação de senha para: {}", destinatario);
            mailSender.send(message);
            log.info("E-mail de recuperação enviado com sucesso para: {}", destinatario);
        } catch (Exception ex) {
            log.error("Falha ao enviar e-mail para {}: {}", destinatario, ex.getMessage(), ex);
            throw new BusinessException("Falha ao enviar e-mail: " + ex.getMessage());
        }
    }
}
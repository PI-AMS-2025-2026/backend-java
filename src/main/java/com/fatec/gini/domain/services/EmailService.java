package com.fatec.gini.domain.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@gini.local}")
    private String remetente;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public void enviarRecuperacaoSenha(String destinatario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remetente);
        message.setTo(destinatario);
        message.setSubject("Recuperação de senha - GINI");
        message.setText("Olá!\n\n"
                + "Use o link abaixo para redefinir sua senha. Ele expira em poucos minutos e só pode ser usado uma vez.\n\n"
                + frontendUrl + "/redefinir-senha?token=" + token + "\n\n"
                + "Se você não solicitou essa alteração, ignore este e-mail.");
        mailSender.send(message);
    }
}
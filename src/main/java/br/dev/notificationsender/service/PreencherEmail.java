package br.dev.notificationsender.service;

import br.dev.notificationsender.events.contratos.EmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;

@Slf4j
public class PreencherEmail {

    public MimeMessage preencher(String from, EmailDTO emailDTO, JavaMailSender mailSender) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        preencherDestinatarios(emailDTO, helper);
        preencherAssunto(emailDTO, helper);
        preencherConteudo(emailDTO, helper);
        adicionaAnexoCasoExista(emailDTO, helper);

        return message;
    }

    private void preencherDestinatarios(EmailDTO emailDTO, MimeMessageHelper helper) throws MessagingException {
        if (emailDTO.getTo() == null || emailDTO.getTo().isEmpty()) {
            throw new IllegalArgumentException("Lista de destinatários está vazia.");
        }

        helper.setTo(emailDTO.getTo().toArray(new String[0]));
    }

    private void preencherAssunto(EmailDTO emailDTO, MimeMessageHelper helper) throws MessagingException {
        if (emailDTO.getSubject() == null || emailDTO.getSubject().isEmpty()) {
            throw new IllegalArgumentException("Não há assunto para o e-mail.");
        }

        helper.setSubject(emailDTO.getSubject());
    }

    private void preencherConteudo(EmailDTO emailDTO, MimeMessageHelper helper) throws MessagingException {
        if (emailDTO.getMessage() == null || emailDTO.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Não há conteúdo no corpo do e-mail.");
        }

        helper.setText(emailDTO.getMessage(), true);
    }

    private void adicionaAnexoCasoExista(EmailDTO emailDTO, MimeMessageHelper helper) throws MessagingException {
        if (emailDTO.getAttachmentsAbsolutePath() != null && !emailDTO.getAttachmentsAbsolutePath().isEmpty()) {
            File anexo = new File(emailDTO.getAttachmentsAbsolutePath());

            if (anexo.exists() && anexo.isFile()) {
                helper.addAttachment(emailDTO.getAttachmentsNameToBeDisplayed(), anexo);
            } else {
                log.warn("Arquivo de anexo não encontrado: {}", emailDTO.getAttachmentsAbsolutePath());
            }
        }
    }

}

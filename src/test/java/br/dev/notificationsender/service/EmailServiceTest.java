package br.dev.notificationsender.service;

import br.dev.notificationsender.events.contratos.EmailDTO;
import br.dev.notificationsender.exceptions.EmailSendingFailureException;
import br.dev.notificationsender.service.validations.EmailDTOValidator;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Proxy;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailServiceTest {

    @Test
    void deveMontarEEnviarEmailValido() {
        JavaMailSenderFake mailSender = new JavaMailSenderFake();
        EmailService service = service(mailSender.proxy());
        EmailDTO emailDTO = validEmailDTO();

        service.enviarEmail(emailDTO);

        assertThat(mailSender.sentMessage).isNotNull();
    }

    @Test
    void deveConverterFalhaDoJavaMailSenderEmExcecaoDeDominio() {
        JavaMailSenderFake mailSender = new JavaMailSenderFake();
        mailSender.exceptionToThrow = new MailSendException("smtp indisponivel");
        EmailService service = service(mailSender.proxy());

        assertThatThrownBy(() -> service.enviarEmail(validEmailDTO()))
                .isInstanceOf(EmailSendingFailureException.class)
                .hasCauseInstanceOf(MailSendException.class);
    }

    private static EmailService service(JavaMailSender mailSender) {
        EmailService service = new EmailService(mailSender, new EmailDTOValidator(), new MimeMessageFactory());
        ReflectionTestUtils.setField(service, "from", "no-reply@example.com");
        return service;
    }

    private static EmailDTO validEmailDTO() {
        return EmailDTO.builder()
                .to(List.of("morador@example.com"))
                .subject("Fatura emitida")
                .message("<p>Mensagem</p>")
                .build();
    }

    private static final class JavaMailSenderFake {

        private MimeMessage sentMessage;

        private RuntimeException exceptionToThrow;

        private JavaMailSender proxy() {
            return (JavaMailSender) Proxy.newProxyInstance(
                    JavaMailSender.class.getClassLoader(),
                    new Class<?>[]{JavaMailSender.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "createMimeMessage" -> createMimeMessage();
                        case "send" -> send(args);
                        default -> throw new UnsupportedOperationException("Metodo nao implementado no fake: " + method.getName());
                    }
            );
        }

        private MimeMessage createMimeMessage() {
            return new MimeMessage((Session) null);
        }

        private Object send(Object[] args) {
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }

            sentMessage = (MimeMessage) args[0];
            return null;
        }
    }
}

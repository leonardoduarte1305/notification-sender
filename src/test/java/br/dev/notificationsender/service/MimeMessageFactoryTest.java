package br.dev.notificationsender.service;

import br.dev.notificationsender.events.contratos.EmailDTO;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Proxy;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MimeMessageFactoryTest {

    private final MimeMessageFactory factory = new MimeMessageFactory();

    @Test
    void devePreencherRemetenteDestinatariosAssuntoEConteudo() throws Exception {
        MimeMessage message = factory.preencher(
                "no-reply@example.com",
                EmailDTO.builder()
                        .to(List.of("morador@example.com"))
                        .subject("Fatura emitida")
                        .message("<p>Mensagem</p>")
                        .build(),
                mailSender()
        );

        assertThat(message.getFrom()).containsExactly(new InternetAddress("no-reply@example.com"));
        assertThat(message.getRecipients(Message.RecipientType.TO)).containsExactly(new InternetAddress("morador@example.com"));
        assertThat(message.getSubject()).isEqualTo("Fatura emitida");
        assertThat(extractText(message)).contains("Mensagem");
    }

    private static String extractText(Part part) throws Exception {
        Object content = part.getContent();

        if (content instanceof String text) {
            return text;
        }

        if (content instanceof Multipart multipart) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                text.append(extractText(multipart.getBodyPart(i)));
            }
            return text.toString();
        }

        return String.valueOf(content);
    }

    private static JavaMailSender mailSender() {
        return (JavaMailSender) Proxy.newProxyInstance(
                JavaMailSender.class.getClassLoader(),
                new Class<?>[]{JavaMailSender.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "createMimeMessage" -> new MimeMessage((Session) null);
                    default -> throw new UnsupportedOperationException("Metodo nao implementado no fake: " + method.getName());
                }
        );
    }
}

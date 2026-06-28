package br.dev.notificationsender.service.validations;

import br.dev.notificationsender.events.contratos.EmailDTO;
import br.dev.notificationsender.exceptions.InvalidEmailDTOException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.dev.notificationsender.commons.ErrorMessages.CAMPO_NAO_PODE_SER_NULO_OU_VAZIO;
import static br.dev.notificationsender.commons.ErrorMessages.CAMPO_OBRIGATORIO;
import static br.dev.notificationsender.commons.ErrorMessages.DEVE_SER_EMAIL_VALIDO;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailDTOValidatorTest {

    private final EmailDTOValidator validator = new EmailDTOValidator();

    @Test
    void deveAceitarEmailDTOValido() {
        assertThatCode(() -> validator.validate(validEmailDTO()))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarEmailDTONulo() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(InvalidEmailDTOException.class)
                .hasMessageContaining("não pode ser nulo");
    }

    @Test
    void deveRejeitarCamposObrigatoriosAusentes() {
        EmailDTO emailDTO = new EmailDTO();

        assertThatThrownBy(() -> validator.validate(emailDTO))
                .isInstanceOf(InvalidEmailDTOException.class)
                .hasMessageContaining(CAMPO_NAO_PODE_SER_NULO_OU_VAZIO.format("subject"))
                .hasMessageContaining(CAMPO_NAO_PODE_SER_NULO_OU_VAZIO.format("message"))
                .hasMessageContaining(CAMPO_OBRIGATORIO.format("to"));
    }

    @Test
    void deveRejeitarCamposObrigatoriosEmBranco() {
        EmailDTO emailDTO = EmailDTO.builder()
                .subject(" ")
                .message(" ")
                .to(List.of(" "))
                .build();

        assertThatThrownBy(() -> validator.validate(emailDTO))
                .isInstanceOf(InvalidEmailDTOException.class)
                .hasMessageContaining(CAMPO_NAO_PODE_SER_NULO_OU_VAZIO.format("subject"))
                .hasMessageContaining(CAMPO_NAO_PODE_SER_NULO_OU_VAZIO.format("message"))
                .hasMessageContaining(CAMPO_NAO_PODE_SER_NULO_OU_VAZIO.format("destinatário"));
    }

    @Test
    void deveRejeitarDestinatarioInvalido() {
        EmailDTO emailDTO = validEmailDTO();
        String emailInvalido = "email-invalido";
        emailDTO.setTo(List.of(emailInvalido));

        assertThatThrownBy(() -> validator.validate(emailDTO))
                .isInstanceOf(InvalidEmailDTOException.class)
                .hasMessageContaining(DEVE_SER_EMAIL_VALIDO.format(emailInvalido));
    }

    @Test
    void deveRejeitarDestinatarioNulo() {
        EmailDTO emailDTO = validEmailDTO();
        emailDTO.setTo(new java.util.ArrayList<>());
        emailDTO.getTo().add(null);

        assertThatThrownBy(() -> validator.validate(emailDTO))
                .isInstanceOf(InvalidEmailDTOException.class)
                .hasMessageContaining(CAMPO_NAO_PODE_SER_NULO_OU_VAZIO.format("destinatário"));
    }

    private static EmailDTO validEmailDTO() {
        return EmailDTO.builder()
                .subject("Fatura emitida")
                .message("<p>Mensagem</p>")
                .to(List.of("morador@example.com"))
                .build();
    }

}

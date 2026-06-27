package br.dev.notificationsender.service.validations;

import br.dev.notificationsender.events.contratos.EmailDTO;
import br.dev.notificationsender.exceptions.InvalidEmailDTOException;
import org.junit.jupiter.api.Test;

import java.util.List;

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
                .hasMessageContaining("\"subject\" não pode ser nulo ou estar vazio")
                .hasMessageContaining("\"message\" não pode ser nula ou estar vazia")
                .hasMessageContaining("\"to\" é obrigatório");
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
                .hasMessageContaining("\"subject\" não pode ser nulo ou estar vazio")
                .hasMessageContaining("\"message\" não pode ser nula ou estar vazia")
                .hasMessageContaining("destinatário não pode ser nulo ou estar vazio");
    }

    @Test
    void deveRejeitarDestinatarioInvalido() {
        EmailDTO emailDTO = validEmailDTO();
        emailDTO.setTo(List.of("email-invalido"));

        assertThatThrownBy(() -> validator.validate(emailDTO))
                .isInstanceOf(InvalidEmailDTOException.class)
                .hasMessageContaining("destinatário encontrado com formato invalido: email-invalido");
    }

    @Test
    void deveRejeitarDestinatarioNulo() {
        EmailDTO emailDTO = validEmailDTO();
        emailDTO.setTo(new java.util.ArrayList<>());
        emailDTO.getTo().add(null);

        assertThatThrownBy(() -> validator.validate(emailDTO))
                .isInstanceOf(InvalidEmailDTOException.class)
                .hasMessageContaining("destinatário não pode ser nulo ou estar vazio");
    }

    private static EmailDTO validEmailDTO() {
        return EmailDTO.builder()
                .subject("Fatura emitida")
                .message("<p>Mensagem</p>")
                .to(List.of("morador@example.com"))
                .build();
    }

}

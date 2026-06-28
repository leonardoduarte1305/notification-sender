package br.dev.notificationsender.events.contratos.factories;

import br.dev.notificationsender.events.contratos.EmailEvent;
import br.dev.notificationsender.events.contratos.FaturaEmitidaEvent;
import br.dev.notificationsender.events.contratos.factories.templates.DadosEmail;
import br.dev.notificationsender.events.contratos.factories.templates.FaturaEmitidaBody;
import br.dev.notificationsender.exceptions.InvalidEventTypeException;
import lombok.NoArgsConstructor;

import static br.dev.notificationsender.commons.ErrorMessages.EVENTO_NAO_PODE_SER_NULO;
import static br.dev.notificationsender.commons.ErrorMessages.TIPO_EVENTO_DESCONHECIDO;
import static br.dev.notificationsender.events.contratos.enumx.EventType.FATURA_EMITIDA;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class GeradorCorpoEmailFactory {

    public static DadosEmail criarNotificacaoByEventType(EmailEvent payload) {
        if (isNull(payload)) {
            throw new InvalidEventTypeException(EVENTO_NAO_PODE_SER_NULO.getMessage());
        }

        if (FATURA_EMITIDA.equals(payload.getEventType())) {
            return new FaturaEmitidaBody((FaturaEmitidaEvent) payload);
        }

        throw new InvalidEventTypeException(TIPO_EVENTO_DESCONHECIDO.format(payload.getClass().getSimpleName()));
    }

}

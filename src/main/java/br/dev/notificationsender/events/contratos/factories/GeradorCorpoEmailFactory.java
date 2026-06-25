package br.dev.notificationsender.events.contratos.factories;

import br.dev.notificationsender.events.contratos.EmailEvent;
import br.dev.notificationsender.events.contratos.FaturaEmitidaEvent;
import br.dev.notificationsender.events.contratos.factories.templates.DadosEmail;
import br.dev.notificationsender.events.contratos.factories.templates.FaturaEmitidaBody;
import br.dev.notificationsender.exceptions.InvalidEventTypeException;
import lombok.NoArgsConstructor;

import static br.dev.notificationsender.events.contratos.enumx.EventType.FATURA_EMITIDA;
import static br.dev.notificationsender.exceptions.ErrorMessages.EVENTO_NAO_PODE_SER_NULO;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class GeradorCorpoEmailFactory {

    public static DadosEmail criarNotificacaoByEventType(EmailEvent payload) {
        if (isNull(payload)) {
            throw new InvalidEventTypeException(EVENTO_NAO_PODE_SER_NULO);
        }

        if (FATURA_EMITIDA.equals(payload.getEventType())) {
            return new FaturaEmitidaBody((FaturaEmitidaEvent) payload);
        }

        throw new InvalidEventTypeException(format("Tipo de evento nao conhecido: :%s", payload.getClass().getSimpleName()));
    }

}

package br.dev.notificationsender.events;

import br.dev.notificationsender.events.contratos.EmailDTO;
import br.dev.notificationsender.events.contratos.FaturaEmitidaEvent;
import br.dev.notificationsender.events.contratos.factories.templates.DadosEmail;
import br.dev.notificationsender.events.validation.FaturaEmitidaEventValidator;
import br.dev.notificationsender.exceptions.NonRetryableMessageException;
import br.dev.notificationsender.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static br.dev.notificationsender.events.contratos.factories.GeradorCorpoEmailFactory.criarNotificacaoByEventType;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService service;

    private final ProcessedEmailEventService processedEmailEventService;

    private final FaturaEmitidaEventValidator validator;

    @KafkaListener(topics = "topico-envio-email")
    public void consumirEmissaoDeFaturas(FaturaEmitidaEvent payload) {
        boolean eventoReservado = false;

        try {
            validator.validate(payload);

            eventoReservado = processedEmailEventService.reservarParaProcessamento(payload.eventId(), payload.eventType());
            if (!eventoReservado) {
                return;
            }

            log.info("Consumindo evento de e-mail. eventId={}, eventType={}", payload.eventId(), payload.eventType());

            DadosEmail dadosEmail = criarNotificacaoByEventType(payload);
            EmailDTO emailDTO = generateEmailDTO(payload, dadosEmail);
            service.enviarEmail(emailDTO);

            processedEmailEventService.marcarComoFinalizado(payload.eventId(), payload.eventType());
        } catch (NonRetryableMessageException e) {
            if (eventoReservado) {
                processedEmailEventService.marcarComoFalha(payload.eventId(), payload.eventType());
            }

            log.warn("Mensagem Kafka invalida. eventId={}, eventType={}, motivo={}",
                    payload != null ? payload.eventId() : null,
                    payload != null ? payload.eventType() : null,
                    e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            if (eventoReservado) {
                processedEmailEventService.marcarComoFalha(payload.eventId(), payload.eventType());
            }

            log.error("Falha ao consumir evento de e-mail. eventId={}, eventType={}",
                    payload != null ? payload.eventId() : null,
                    payload != null ? payload.eventType() : null,
                    e);
            throw e;
        }
    }

    private static EmailDTO generateEmailDTO(FaturaEmitidaEvent payload, DadosEmail dadosEmail) {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(List.of(payload.destinatario()));
        emailDTO.setSubject(dadosEmail.getSubject());
        emailDTO.setMessage(dadosEmail.getMessage());
        return emailDTO;
    }

}

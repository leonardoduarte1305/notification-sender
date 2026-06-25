package br.dev.notificationsender.events;

import br.dev.notificationsender.events.contratos.EmailDTO;
import br.dev.notificationsender.events.contratos.FaturaEmitidaEvent;
import br.dev.notificationsender.events.contratos.factories.templates.DadosEmail;
import br.dev.notificationsender.events.entity.ProcessedEmailEvent;
import br.dev.notificationsender.events.repository.ProcessedEmailEventRepository;
import br.dev.notificationsender.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.BytesJacksonJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static br.dev.notificationsender.events.contratos.enumx.EventStatus.FINISHED;
import static br.dev.notificationsender.events.contratos.factories.GeradorCorpoEmailFactory.criarNotificacaoByEventType;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService service;

    private final ProcessedEmailEventRepository repository;

    private static EmailDTO generateEmailDTO(FaturaEmitidaEvent payload, DadosEmail dadosEmail) {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(List.of(payload.destinatario()));
        emailDTO.setSubject(dadosEmail.getSubject());
        emailDTO.setMessage(dadosEmail.getMessage());
        return emailDTO;
    }

    @Bean
    public RecordMessageConverter converter() {
        return new BytesJacksonJsonMessageConverter();
    }

    @KafkaListener(topics = "topico-envio-email")
    public void consumirEmissaoDeFaturas(FaturaEmitidaEvent payload) {
        try {
            if (eventoJaProcessado(payload.eventId())) {
                log.info("Evento de e-mail ja processado. eventId={}, eventType={}", payload.eventId(), payload.eventType());
                return;
            }

            DadosEmail dadosEmail = criarNotificacaoByEventType(payload);
            EmailDTO emailDTO = generateEmailDTO(payload, dadosEmail);
            service.enviarEmail(emailDTO).join();

            ProcessedEmailEvent eventoProcessado = new ProcessedEmailEvent(payload.eventId(), payload.eventType(), FINISHED);
            repository.save(eventoProcessado);
        } catch (RuntimeException e) {
            log.error("Falha ao consumir evento de e-mail. eventId={}, eventType={}",
                    payload != null ? payload.eventId() : null,
                    payload != null ? payload.eventType() : null,
                    e);
            throw e;
        }
    }

    private boolean eventoJaProcessado(UUID eventId) {
        return repository.existsByEventIdAndStatus(eventId, FINISHED);
    }

}

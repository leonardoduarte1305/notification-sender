package br.dev.notificationsender.events;

import br.dev.notificationsender.exceptions.NonRetryableMessageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    private static final String TOPICO_DLQ = "topico-dlq";

    @Bean
    public DefaultErrorHandler errorHandler() {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (letter, exception) -> {
                    log.error("Mensagem enviada para DLQ. topic={}, partition={}, offset={}, dlqTopic={}, exception={}",
                            letter.topic(),
                            letter.partition(),
                            letter.offset(),
                            TOPICO_DLQ,
                            exception.getClass().getSimpleName());
                    return new TopicPartition(TOPICO_DLQ, letter.partition());
                }
        );

        FixedBackOff backOff = new FixedBackOff(1000L, 3L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions(NonRetryableMessageException.class);

        return errorHandler;
    }

}

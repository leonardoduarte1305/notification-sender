package br.dev.notificationsender.commons;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessages implements MessageFormatter {

    EVENTO_NAO_PODE_SER_NULO("Evento não pode ser nulo"),

    CAMPO_NAO_PODE_SER_NULO("%s não pode ser nulo"),

    CAMPO_NAO_PODE_SER_NULO_OU_VAZIO("%s não pode ser nulo ou estar vazio"),

    CAMPO_NAO_PODE_SER_NULO_OU_VAZIO_OU_BLANK("%s não pode ser nulo, vazio ou estar em branco"),

    TIPO_EVENTO_DESCONHECIDO("Tipo de evento não conhecido: :%s"),

    PAYLOAD_NAO_PODE_SER_NULO("Payload do evento de fatura emitida não pode ser nulo."),

    PAYLOAD_FATURA_EMITIDA_INVALIDO("Payload do evento de fatura emitida invalido: %s"),

    CAMPO_OBRIGATORIO("%s é obrigatório"),

    DEVE_SER_POSITIVO("%s deve ser positivo"),

    DEVE_SER_MAIOR_QUE_ZERO("%s deve ser maior que zero"),

    DEVE_SER_EMAIL_VALIDO("%s deve ser um e-mail valido");

    private final String message;

}

package br.dev.notificationsender.configuration.xapikey;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApiKeyConfiguration {

    @Value("${app.security.x-api-key}")
    private String xApiKey;

}

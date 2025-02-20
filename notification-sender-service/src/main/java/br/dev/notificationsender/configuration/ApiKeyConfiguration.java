package br.dev.notificationsender.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Getter
@Component
@Profile("prod")
public class ApiKeyConfiguration {

    @Value("${app.security.x-api-key}")
    private String xApiKey;

}

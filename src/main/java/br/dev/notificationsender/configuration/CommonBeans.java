package br.dev.notificationsender.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonBeans {

    @Bean
    public Module javaTimeModule() {
        return new JavaTimeModule();
    }

}

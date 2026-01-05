package com.example.userservice.configuration;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class config {

    @Bean // Чтобы можно было внедрять (inject) в сервис
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://keycloak:8080")
                .realm("master") // Админ обычно живет в master, но управляет всеми
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .grantType("password") // Указываем тип авторизации
                .build();
    }

}

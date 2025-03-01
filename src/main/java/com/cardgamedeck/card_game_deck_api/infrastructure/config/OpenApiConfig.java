package com.cardgamedeck.card_game_deck_api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cardGameOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Card Game API")
                        .description("REST API for card game operations")
                        .version("v1.0.0"));
    }
}
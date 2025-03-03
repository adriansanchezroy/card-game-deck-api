package com.cardgamedeck.card_game_deck_api.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.cardgamedeck.card_game_deck_api.domain.repository"
)
@EnableTransactionManagement
public class JpaConfig {
    //  Spring Boot handles most of JPA configuration
}

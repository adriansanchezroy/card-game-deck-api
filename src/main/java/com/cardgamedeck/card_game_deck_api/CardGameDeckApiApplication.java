package com.cardgamedeck.card_game_deck_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("!cli")
public class CardGameDeckApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(CardGameDeckApiApplication.class, args);
	}
}

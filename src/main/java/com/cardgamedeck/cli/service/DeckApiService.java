package com.cardgamedeck.cli.service;

import com.cardgamedeck.cli.config.CliConfig;
import com.cardgamedeck.card_game_deck_api.presentation.dto.DeckDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class DeckApiService extends BaseApiService {

    private static final String DECKS_ENDPOINT = "/decks";

    public DeckApiService(RestTemplate restTemplate, CliConfig cliConfig) {
        super(restTemplate, cliConfig.getApiBaseUrl());
    }

    public List<DeckDTO> getAllDecks() {
        DeckDTO[] decks = get(DECKS_ENDPOINT, DeckDTO[].class);
        return decks != null ? Arrays.asList(decks) : null;
    }

    public DeckDTO getDeckById(UUID deckId) {
        return get(DECKS_ENDPOINT + "/" + deckId, DeckDTO.class);
    }

    public DeckDTO createDeck(String name) {
        DeckDTO request = new DeckDTO();
        request.setName(name);
        return post(DECKS_ENDPOINT, request, DeckDTO.class);
    }

}
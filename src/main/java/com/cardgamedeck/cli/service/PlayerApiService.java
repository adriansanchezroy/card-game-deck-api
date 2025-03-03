package com.cardgamedeck.cli.service;

import com.cardgamedeck.cli.config.CliConfig;
import com.cardgamedeck.card_game_deck_api.presentation.dto.PlayerDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerCardsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class PlayerApiService extends BaseApiService {

    private static final String PLAYERS_ENDPOINT = "/players";

    public PlayerApiService(RestTemplate restTemplate, CliConfig cliConfig) {
        super(restTemplate, cliConfig.getApiBaseUrl());
    }

    public List<PlayerDTO> getAllPlayers() {
        PlayerDTO[] players = get(PLAYERS_ENDPOINT, PlayerDTO[].class);
        return players != null ? Arrays.asList(players) : null;
    }

    public PlayerDTO getPlayerById(UUID playerId) {
        return get(PLAYERS_ENDPOINT + "/" + playerId, PlayerDTO.class);
    }

    public PlayerDTO createPlayer(String name) {
        PlayerDTO request = new PlayerDTO();
        request.setName(name);
        return post(PLAYERS_ENDPOINT, request, PlayerDTO.class);
    }

    public PlayerCardsResponse getPlayerCards(UUID playerId) {
        return get(PLAYERS_ENDPOINT + "/" + playerId + "/cards", PlayerCardsResponse.class);
    }

    // Additional method if your API supports clearing player cards
    public PlayerDTO clearPlayerCards(UUID playerId) {
        return post(PLAYERS_ENDPOINT + "/" + playerId + "/clear-cards", null, PlayerDTO.class);
    }
}
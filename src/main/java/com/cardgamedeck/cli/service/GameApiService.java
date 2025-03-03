package com.cardgamedeck.cli.service;

import com.cardgamedeck.cli.config.CliConfig;
import com.cardgamedeck.card_game_deck_api.presentation.dto.GameDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.request.*;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountBySuitResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountByValueResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerScoreResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class GameApiService extends BaseApiService {

    private static final String GAMES_ENDPOINT = "/games";

    public GameApiService(RestTemplate restTemplate, CliConfig cliConfig) {
        super(restTemplate, cliConfig.getApiBaseUrl());
    }

    public List<GameDTO> getAllGames() {
        GameDTO[] games = get(GAMES_ENDPOINT, GameDTO[].class);
        return games != null ? Arrays.asList(games) : null;
    }

    public GameDTO getGameById(UUID gameId) {
        return get(GAMES_ENDPOINT + "/" + gameId, GameDTO.class);
    }

    public GameDTO createGame(String name) {
        CreateGameRequest request = new CreateGameRequest(name);  // Use GameRequest here
        return post(GAMES_ENDPOINT, request, GameDTO.class);
    }

    public void deleteGame(UUID gameId) {
        delete(GAMES_ENDPOINT + "/" + gameId);
    }

    public GameDTO addDeckToGame(UUID gameId, UUID deckId) {
        return post(GAMES_ENDPOINT + "/" + gameId + "/decks/" + deckId, null, GameDTO.class);
    }

    public GameDTO addPlayerToGame(UUID gameId, UUID playerId) {
        return post(GAMES_ENDPOINT + "/" + gameId + "/players/" + playerId, null, GameDTO.class);
    }

    public GameDTO removePlayerFromGame(UUID gameId, UUID playerId) {
        return delete(GAMES_ENDPOINT + "/" + gameId + "/players/" + playerId, GameDTO.class);
    }

    public GameDTO dealCardsToPlayer(UUID gameId, UUID playerId, int count) {
        DealCardsRequest request = new DealCardsRequest();
        request.setCount(count);
        return post(GAMES_ENDPOINT + "/" + gameId + "/players/" + playerId + "/deal", request, GameDTO.class);
    }

    public List<PlayerScoreResponse> getPlayerScores(UUID gameId) {
        PlayerScoreResponse[] scores = get(GAMES_ENDPOINT + "/" + gameId + "/players/scores", PlayerScoreResponse[].class);
        return scores != null ? Arrays.asList(scores) : null;
    }

    public CardCountBySuitResponse getUndealtCardsBySuit(UUID gameId) {
        return get(GAMES_ENDPOINT + "/" + gameId + "/deck/cards-by-suit", CardCountBySuitResponse.class);
    }

    public CardCountByValueResponse getUndealtCardsByValue(UUID gameId) {
        return get(GAMES_ENDPOINT + "/" + gameId + "/deck/cards-by-value", CardCountByValueResponse.class);
    }

    public GameDTO shuffleGameDeck(UUID gameId) {
        return post(GAMES_ENDPOINT + "/" + gameId + "/deck/shuffle", null, GameDTO.class);
    }

    // Additional method to handle DELETE with response body
    private <T> T delete(String endpoint, Class<T> responseType) {
        try {
            return restTemplate.exchange(
                    baseUrl + endpoint,
                    HttpMethod.DELETE,
                    null,
                    responseType
            ).getBody();
        } catch (HttpStatusCodeException e) {
            handleApiError(e);
            return null;
        } catch (RestClientException e) {
            handleConnectionError(e);
            return null;
        }
    }
}
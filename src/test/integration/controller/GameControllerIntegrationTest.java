package integration.controller;

import com.cardgamedeck.card_game_deck_api.CardGameDeckApiApplication;
import com.cardgamedeck.card_game_deck_api.domain.model.Game;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.application.api.IGameService;
import com.cardgamedeck.card_game_deck_api.presentation.controller.GameController;
import com.cardgamedeck.card_game_deck_api.presentation.dto.GameDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.request.CreateGameRequest;
import com.cardgamedeck.card_game_deck_api.presentation.dto.request.DealCardsRequest;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountBySuitResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountByValueResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerScoreResponse;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.GameMapper;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.PlayerMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
@ContextConfiguration(classes = CardGameDeckApiApplication.class)
public class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IGameService gameService;

    @MockBean
    private GameMapper gameMapper;

    @MockBean
    private PlayerMapper playerMapper;

    @Test
    void createGame_ShouldReturnCreatedGame() throws Exception {
        // Setup
        CreateGameRequest request = new CreateGameRequest("Test Game");
        Game game = new Game("Test Game");
        GameDTO gameDTO = new GameDTO(UUID.randomUUID(), "Test Game", 0, 0);

        when(gameService.createGame("Test Game")).thenReturn(game);
        when(gameMapper.toDTO(game)).thenReturn(gameDTO);

        // Execute & Verify
        mockMvc.perform(post("/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Game")))
                .andExpect(jsonPath("$.playerCount", is(0)))
                .andExpect(jsonPath("$.undealtCardCount", is(0)));
    }

    @Test
    void addDeckToGame_ShouldAddDeckCardsToGameDeck() throws Exception {
        // Setup
        UUID gameId = UUID.randomUUID();
        UUID deckId = UUID.randomUUID();
        Game game = new Game("Game With Deck");
        GameDTO gameDTO = new GameDTO(gameId, "Game With Deck", 52, 0);

        when(gameService.addDeckToGame(gameId, deckId)).thenReturn(game);
        when(gameMapper.toDTO(game)).thenReturn(gameDTO);

        // Execute & Verify
        mockMvc.perform(post("/games/{gameId}/decks/{deckId}", gameId, deckId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.undealtCardCount", is(52)));
    }

    @Test
    void addPlayerToGame_ShouldIncreasePlayerCount() throws Exception {
        // Setup
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        Game game = new Game("Game With Player");
        GameDTO gameDTO = new GameDTO(gameId, "Game With Player", 0, 1);

        when(gameService.addPlayerToGame(gameId, playerId)).thenReturn(game);
        when(gameMapper.toDTO(game)).thenReturn(gameDTO);

        // Execute & Verify
        mockMvc.perform(post("/games/{gameId}/players/{playerId}", gameId, playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerCount", is(1)));
    }

    @Test
    void dealCardsToPlayer_ShouldTransferCards() throws Exception {
        // Setup
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        Game game = new Game("Deal Cards Game");
        GameDTO gameDTO = new GameDTO(gameId, "Deal Cards Game", 47, 1);

        DealCardsRequest dealRequest = new DealCardsRequest();
        dealRequest.setCount(5);

        when(gameService.dealCardsToPlayer(eq(gameId), eq(playerId), anyInt())).thenReturn(game);
        when(gameMapper.toDTO(game)).thenReturn(gameDTO);

        // Execute & Verify
        mockMvc.perform(post("/games/{gameId}/players/{playerId}/deal", gameId, playerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.undealtCardCount", is(47)));
    }

    @Test
    void shuffleGameDeck_ShouldNotChangeCardCount() throws Exception {
        // Setup
        UUID gameId = UUID.randomUUID();
        Game game = new Game("Shuffle Game");
        GameDTO gameDTO = new GameDTO(gameId, "Shuffle Game", 52, 0);

        when(gameService.shuffleGameDeck(gameId)).thenReturn(game);
        when(gameMapper.toDTO(game)).thenReturn(gameDTO);

        // Execute & Verify
        mockMvc.perform(post("/games/{gameId}/deck/shuffle", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.undealtCardCount", is(52)));
    }

    @Test
    void getPlayerScores_ShouldReturnSortedPlayersByTotalValue() throws Exception {
        // Setup
        UUID gameId = UUID.randomUUID();
        List<Player> players = new ArrayList<>();

        Player player1 = new Player("Player One");
        Player player2 = new Player("Player Two");
        players.add(player1);
        players.add(player2);

        List<PlayerScoreResponse> playerScores = new ArrayList<>();
        playerScores.add(new PlayerScoreResponse(UUID.randomUUID(), "Player One", 20, 3));
        playerScores.add(new PlayerScoreResponse(UUID.randomUUID(), "Player Two", 15, 2));

        when(gameService.getPlayersWithTotalValues(gameId)).thenReturn(players);
        when(playerMapper.toScoreDTO(player1)).thenReturn(playerScores.get(0));
        when(playerMapper.toScoreDTO(player2)).thenReturn(playerScores.get(1));

        // Execute & Verify
        MvcResult result = mockMvc.perform(get("/games/{gameId}/players/scores", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();

        // Extract values and manually verify sorting
        String content = result.getResponse().getContentAsString();
        var scores = objectMapper.readTree(content);
        int firstValue = scores.get(0).get("totalValue").asInt();
        int secondValue = scores.get(1).get("totalValue").asInt();

        // First score should be greater than or equal to second score
        assertTrue(firstValue >= secondValue, "Scores should be sorted in descending order");
    }

    @Test
    void getUndealtCardsBySuit_ShouldReturnCorrectCounts() throws Exception {
        // Setup
        UUID gameId = UUID.randomUUID();
        Map<Suit, Integer> suitCounts = new HashMap<>();
        suitCounts.put(Suit.HEARTS, 13);
        suitCounts.put(Suit.SPADES, 13);
        suitCounts.put(Suit.CLUBS, 13);
        suitCounts.put(Suit.DIAMONDS, 13);

        CardCountBySuitResponse response = new CardCountBySuitResponse(suitCounts);

        when(gameService.getUndealtCardsBySuit(gameId)).thenReturn(suitCounts);
        when(gameMapper.toCardCountBySuitResponse(suitCounts)).thenReturn(response);

        // Execute & Verify
        mockMvc.perform(get("/games/{gameId}/deck/cards-by-suit", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardCountBySuit.HEARTS", is(13)))
                .andExpect(jsonPath("$.cardCountBySuit.SPADES", is(13)))
                .andExpect(jsonPath("$.cardCountBySuit.CLUBS", is(13)))
                .andExpect(jsonPath("$.cardCountBySuit.DIAMONDS", is(13)));
    }

    @Test
    void getUndealtCardsByValue_ShouldReturnCorrectCounts() throws Exception {
        // Setup
        UUID gameId = UUID.randomUUID();
        Map<String, Integer> valueCounts = new HashMap<>();
        valueCounts.put("HEARTS-ACE", 1);
        valueCounts.put("SPADES-KING", 1);
        valueCounts.put("CLUBS-QUEEN", 1);

        CardCountByValueResponse response = new CardCountByValueResponse(valueCounts);

        when(gameService.getUndealtCardsBySuitAndValue(gameId)).thenReturn(valueCounts);
        when(gameMapper.toCardCountByValueResponse(valueCounts)).thenReturn(response);

        // Execute & Verify
        mockMvc.perform(get("/games/{gameId}/deck/cards-by-value", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardCountByValue['HEARTS-ACE']", is(1)))
                .andExpect(jsonPath("$.cardCountByValue['SPADES-KING']", is(1)))
                .andExpect(jsonPath("$.cardCountByValue['CLUBS-QUEEN']", is(1)));
    }

    @Test
    void getGameById_ShouldReturnGame() throws Exception {
        // Setup
        UUID gameId = UUID.randomUUID();
        Game game = new Game("Test Game");
        GameDTO gameDTO = new GameDTO(gameId, "Test Game", 0, 0);

        when(gameService.findById(gameId)).thenReturn(Optional.of(game));
        when(gameMapper.toDTO(game)).thenReturn(gameDTO);

        // Execute & Verify
        mockMvc.perform(get("/games/{gameId}", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(gameId.toString())))
                .andExpect(jsonPath("$.name", is("Test Game")));
    }

    @Test
    void removePlayerFromGame_ShouldReturnUpdatedGame() throws Exception {
        // Setup
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        Game game = new Game("Game After Player Removal");
        GameDTO gameDTO = new GameDTO(gameId, "Game After Player Removal", 52, 0);

        when(gameService.removePlayerFromGame(gameId, playerId)).thenReturn(game);
        when(gameMapper.toDTO(game)).thenReturn(gameDTO);

        // Execute & Verify
        mockMvc.perform(delete("/games/{gameId}/players/{playerId}", gameId, playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerCount", is(0)));
    }
}
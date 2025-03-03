package integration.controller;

import com.cardgamedeck.card_game_deck_api.CardGameDeckApiApplication;
import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Value;
import com.cardgamedeck.card_game_deck_api.application.api.IPlayerService;
import com.cardgamedeck.card_game_deck_api.presentation.controller.PlayerController;
import com.cardgamedeck.card_game_deck_api.presentation.dto.*;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerCardsResponse;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
@ContextConfiguration(classes = CardGameDeckApiApplication.class)
public class PlayerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IPlayerService playerService;

    @MockBean
    private PlayerMapper playerMapper;

    @MockBean
    private CardMapper cardMapper;

    @Test
    void createPlayer_ShouldReturnCreatedPlayer() throws Exception {
        // Setup
        PlayerDTO playerRequest = new PlayerDTO();
        playerRequest.setName("Test Player");

        Player player = new Player("Test Player");

        PlayerDTO playerResponse = new PlayerDTO();
        playerResponse.setId(UUID.randomUUID());
        playerResponse.setName("Test Player");
        playerResponse.setCardCount(0);

        when(playerService.createPlayer("Test Player")).thenReturn(player);
        when(playerMapper.toDTO(player)).thenReturn(playerResponse);

        // Execute & Verify
        mockMvc.perform(post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Player")))
                .andExpect(jsonPath("$.cardCount", is(0)));
    }

    @Test
    void getPlayerById_WithExistingId_ShouldReturnPlayer() throws Exception {
        // Setup
        UUID playerId = UUID.randomUUID();
        Player player = new Player("Test Player");

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(playerId);
        playerDTO.setName("Test Player");
        playerDTO.setCardCount(0);

        when(playerService.findById(playerId)).thenReturn(Optional.of(player));
        when(playerMapper.toDTO(player)).thenReturn(playerDTO);

        // Execute & Verify
        mockMvc.perform(get("/players/{playerId}", playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(playerId.toString())))
                .andExpect(jsonPath("$.name", is("Test Player")))
                .andExpect(jsonPath("$.cardCount", is(0)));
    }

    @Test
    void getPlayerById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Setup
        UUID playerId = UUID.randomUUID();
        when(playerService.findById(playerId)).thenReturn(Optional.empty());

        // Execute & Verify
        mockMvc.perform(get("/players/{playerId}", playerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlayerCards_WithExistingId_ShouldReturnPlayerCards() throws Exception {
        // Setup
        UUID playerId = UUID.randomUUID();
        Player player = new Player("Test Player");

        // Create cards
        Set<Card> playerCards = new HashSet<>();
        Card card1 = new Card(Suit.HEARTS, Value.ACE);
        Card card2 = new Card(Suit.SPADES, Value.KING);
        playerCards.add(card1);
        playerCards.add(card2);

        // Create card DTOs
        Set<CardDTO> cardDTOs = new HashSet<>();
        CardDTO cardDTO1 = new CardDTO();
        cardDTO1.setId(UUID.randomUUID());
        cardDTO1.setSuit(Suit.HEARTS);
        cardDTO1.setValue(Value.ACE);
        cardDTO1.setFaceValue(1);

        CardDTO cardDTO2 = new CardDTO();
        cardDTO2.setId(UUID.randomUUID());
        cardDTO2.setSuit(Suit.SPADES);
        cardDTO2.setValue(Value.KING);
        cardDTO2.setFaceValue(13);

        cardDTOs.add(cardDTO1);
        cardDTOs.add(cardDTO2);

        // Create player cards response
        PlayerCardsResponse playerCardsResponse = new PlayerCardsResponse();
        playerCardsResponse.setPlayerId(playerId);
        playerCardsResponse.setPlayerName("Test Player");
        playerCardsResponse.setCards(cardDTOs);

        when(playerService.findById(playerId)).thenReturn(Optional.of(player));
        when(playerService.getPlayerCards(playerId)).thenReturn(playerCards);
        when(cardMapper.toDTO(card1)).thenReturn(cardDTO1);
        when(cardMapper.toDTO(card2)).thenReturn(cardDTO2);
        when(playerMapper.toCardsResponse(eq(player), any())).thenReturn(playerCardsResponse);

        // Execute & Verify
        mockMvc.perform(get("/players/{playerId}/cards", playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId", is(playerId.toString())))
                .andExpect(jsonPath("$.playerName", is("Test Player")))
                .andExpect(jsonPath("$.cards", hasSize(2)))
                .andExpect(jsonPath("$.cards[*].suit", hasItems("HEARTS", "SPADES")))
                .andExpect(jsonPath("$.cards[*].value", hasItems("ACE", "KING")));
    }

    @Test
    void getPlayerCards_WithNonExistingId_ShouldThrowException() throws Exception {
        // Setup
        UUID playerId = UUID.randomUUID();
        when(playerService.findById(playerId)).thenReturn(Optional.empty());

        // Execute & Verify
        mockMvc.perform(get("/players/{playerId}/cards", playerId))
                .andExpect(status().isInternalServerError());
    }
}
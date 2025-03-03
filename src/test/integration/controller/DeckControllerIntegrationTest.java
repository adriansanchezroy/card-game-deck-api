package integration.controller;

import com.cardgamedeck.card_game_deck_api.CardGameDeckApiApplication;
import com.cardgamedeck.card_game_deck_api.domain.model.Deck;
import com.cardgamedeck.card_game_deck_api.application.api.IDeckService;
import com.cardgamedeck.card_game_deck_api.presentation.controller.DeckController;
import com.cardgamedeck.card_game_deck_api.presentation.dto.DeckDTO;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.DeckMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeckController.class)
@ContextConfiguration(classes = CardGameDeckApiApplication.class)
public class DeckControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IDeckService deckService;

    @MockBean
    private DeckMapper deckMapper;

    @Test
    void createDeck_ShouldReturnCreatedDeck() throws Exception {
        // Setup
        DeckDTO deckRequest = new DeckDTO();
        deckRequest.setName("Standard Deck");

        Deck deck = new Deck("Standard Deck");

        DeckDTO deckResponse = new DeckDTO();
        deckResponse.setId(UUID.randomUUID());
        deckResponse.setName("Standard Deck");
        deckResponse.setCardCount(52);

        when(deckService.createDeck("Standard Deck")).thenReturn(deck);
        when(deckMapper.toDTO(deck)).thenReturn(deckResponse);

        // Execute & Verify
        mockMvc.perform(post("/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deckRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Standard Deck")))
                .andExpect(jsonPath("$.cardCount", is(52)));
    }

    @Test
    void getDeckById_WithExistingId_ShouldReturnDeck() throws Exception {
        // Setup
        UUID deckId = UUID.randomUUID();
        Deck deck = new Deck("Standard Deck");

        DeckDTO deckDTO = new DeckDTO();
        deckDTO.setId(deckId);
        deckDTO.setName("Standard Deck");
        deckDTO.setCardCount(52);

        when(deckService.findById(deckId)).thenReturn(Optional.of(deck));
        when(deckMapper.toDTO(deck)).thenReturn(deckDTO);

        // Execute & Verify
        mockMvc.perform(get("/decks/{deckId}", deckId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(deckId.toString())))
                .andExpect(jsonPath("$.name", is("Standard Deck")))
                .andExpect(jsonPath("$.cardCount", is(52)));
    }

    @Test
    void getDeckById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Setup
        UUID deckId = UUID.randomUUID();
        when(deckService.findById(deckId)).thenReturn(Optional.empty());

        // Execute & Verify
        mockMvc.perform(get("/decks/{deckId}", deckId))
                .andExpect(status().isNotFound());
    }

}
package presentation.mapper;

import com.cardgamedeck.card_game_deck_api.domain.model.Deck;
import com.cardgamedeck.card_game_deck_api.presentation.dto.DeckDTO;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.DeckMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeckMapperTest {

    private DeckMapper deckMapper;
    private Deck testDeck;
    private UUID deckId;

    @BeforeEach
    void setUp() {
        deckMapper = new DeckMapper();

        // Create test deck
        deckId = UUID.randomUUID();
        testDeck = new Deck("Test Deck");
        TestUtils.setPrivateId(testDeck, deckId);
    }

    @Test
    void toDTO_WithValidDeck_ShouldMapAllProperties() {
        // When
        DeckDTO result = deckMapper.toDTO(testDeck);

        // Then
        assertNotNull(result);
        assertEquals(deckId, result.getId());
        assertEquals("Test Deck", result.getName());
        assertEquals(52, result.getCardCount()); // A new deck has 52 cards
    }

    @Test
    void toDTO_WithNullDeck_ShouldReturnNull() {
        // When
        DeckDTO result = deckMapper.toDTO(null);

        // Then
        assertNull(result);
    }

}
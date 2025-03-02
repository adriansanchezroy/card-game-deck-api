package presentation.mapper;

import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Value;
import com.cardgamedeck.card_game_deck_api.presentation.dto.CardDTO;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.CardMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CardMapperTest {

    private CardMapper cardMapper;
    private Card testCard;
    private UUID cardId;

    @BeforeEach
    void setUp() {
        cardMapper = new CardMapper();

        // Create test card
        cardId = UUID.randomUUID();
        testCard = new Card(Suit.HEARTS, Value.ACE);
        TestUtils.setPrivateId(testCard, cardId);
    }

    @Test
    void toDTO_WithValidCard_ShouldMapAllProperties() {
        // When
        CardDTO result = cardMapper.toDTO(testCard);

        // Then
        assertNotNull(result);
        assertEquals(cardId, result.getId());
        assertEquals(Suit.HEARTS, result.getSuit());
        assertEquals(Value.ACE, result.getValue());
        assertEquals(1, result.getFaceValue());
    }

    @Test
    void toDTO_WithNullCard_ShouldReturnNull() {
        // When
        CardDTO result = cardMapper.toDTO(null);

        // Then
        assertNull(result);
    }

}
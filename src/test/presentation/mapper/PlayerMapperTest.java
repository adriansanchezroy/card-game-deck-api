package presentation.mapper;

import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Value;
import com.cardgamedeck.card_game_deck_api.presentation.dto.CardDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.PlayerDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerCardsResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerScoreResponse;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.PlayerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerMapperTest {

    private PlayerMapper playerMapper;
    private Player testPlayer;
    private UUID playerId;
    private Set<CardDTO> cardDTOs;

    @BeforeEach
    void setUp() {
        playerMapper = new PlayerMapper();

        // Create test player
        playerId = UUID.randomUUID();
        testPlayer = new Player("Test Player");
        setPrivateId(testPlayer, playerId);

        // Add some cards to the player
        Card card1 = new Card(Suit.HEARTS, Value.ACE);
        Card card2 = new Card(Suit.SPADES, Value.KING);
        setPrivateId(card1, UUID.randomUUID());
        setPrivateId(card2, UUID.randomUUID());
        testPlayer.addCard(card1);
        testPlayer.addCard(card2);

        // Create CardDTOs for testing
        cardDTOs = new HashSet<>();
        cardDTOs.add(new CardDTO(card1.getId(), card1.getSuit(), card1.getValue(), card1.getFaceValue()));
        cardDTOs.add(new CardDTO(card2.getId(), card2.getSuit(), card2.getValue(), card2.getFaceValue()));
    }

    @Test
    void toDTO_WithValidPlayer_ShouldMapAllProperties() {
        // When
        PlayerDTO result = playerMapper.toDTO(testPlayer);

        // Then
        assertNotNull(result);
        assertEquals(playerId, result.getId());
        assertEquals("Test Player", result.getName());
        assertEquals(2, result.getCardCount());
    }

    @Test
    void toDTO_WithNullPlayer_ShouldReturnNull() {
        // When
        PlayerDTO result = playerMapper.toDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void toScoreDTO_WithValidPlayer_ShouldCalculateTotalValueCorrectly() {
        // When
        PlayerScoreResponse result = playerMapper.toScoreDTO(testPlayer);

        // Then
        assertNotNull(result);
        assertEquals(playerId, result.getPlayerId());
        assertEquals("Test Player", result.getPlayerName());
        assertEquals(14, result.getTotalValue()); // ACE (1) + KING (13)
        assertEquals(2, result.getCardCount());
    }

    @Test
    void toScoreDTO_WithNullPlayer_ShouldReturnNull() {
        // When
        PlayerScoreResponse result = playerMapper.toScoreDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void toCardsResponse_WithValidPlayerAndCards_ShouldMapAllProperties() {
        // When
        PlayerCardsResponse result = playerMapper.toCardsResponse(testPlayer, cardDTOs);

        // Then
        assertNotNull(result);
        assertEquals(playerId, result.getPlayerId());
        assertEquals("Test Player", result.getPlayerName());
        assertEquals(2, result.getCards().size());
        assertTrue(result.getCards().containsAll(cardDTOs));
    }

    @Test
    void toCardsResponse_WithNullPlayer_ShouldReturnNull() {
        // When
        PlayerCardsResponse result = playerMapper.toCardsResponse(null, cardDTOs);

        // Then
        assertNull(result);
    }

    // TODO Put in test Utils
    private void setPrivateId(Object entity, UUID id) {
        try {
            java.lang.reflect.Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            fail("Failed to set up test entity ID: " + e.getMessage());
        }
    }
}
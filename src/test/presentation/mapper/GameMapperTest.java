package presentation.mapper;

import com.cardgamedeck.card_game_deck_api.domain.model.Game;
import com.cardgamedeck.card_game_deck_api.domain.model.GameDeck;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.presentation.dto.GameDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountBySuitResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountByValueResponse;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.GameMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameMapperTest {

    private GameMapper gameMapper;
    private Game testGame;
    private UUID gameId;
    private GameDeck gameDeck;

    @BeforeEach
    void setUp() {
        gameMapper = new GameMapper();

        // Create mock game deck
        gameDeck = Mockito.mock(GameDeck.class);
        Mockito.when(gameDeck.getUndealtCount()).thenReturn(42);

        // Create test game with mock game deck
        gameId = UUID.randomUUID();
        testGame = new Game("Test Game");
        setPrivateId(testGame, gameId);

        // Use reflection to set mock game deck
        try {
            java.lang.reflect.Field gameDeckField = testGame.getClass().getDeclaredField("gameDeck");
            gameDeckField.setAccessible(true);
            gameDeckField.set(testGame, gameDeck);
        } catch (Exception e) {
            fail("Failed to set game deck: " + e.getMessage());
        }

        // Add two players to game
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");
        setPrivateId(player1, UUID.randomUUID());
        setPrivateId(player2, UUID.randomUUID());
        testGame.addPlayer(player1);
        testGame.addPlayer(player2);
    }

    @Test
    void toDTO_WithValidGame_ShouldMapAllProperties() {
        // When
        GameDTO result = gameMapper.toDTO(testGame);

        // Then
        assertNotNull(result);
        assertEquals(gameId, result.getId());
        assertEquals("Test Game", result.getName());
        assertEquals(42, result.getUndealtCardCount());
        assertEquals(2, result.getPlayerCount());
    }

    @Test
    void toDTO_WithNullGame_ShouldReturnNull() {
        // When
        GameDTO result = gameMapper.toDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void toCardCountBySuitResponse_WithValidMap_ShouldCreateResponse() {
        // Given
        Map<Suit, Integer> countBySuit = new HashMap<>();
        countBySuit.put(Suit.HEARTS, 13);
        countBySuit.put(Suit.SPADES, 11);
        countBySuit.put(Suit.CLUBS, 9);
        countBySuit.put(Suit.DIAMONDS, 7);

        // When
        CardCountBySuitResponse result = gameMapper.toCardCountBySuitResponse(countBySuit);

        // Then
        assertNotNull(result);
        assertEquals(countBySuit, result.getCardCountBySuit());
        assertEquals(13, result.getCardCountBySuit().get(Suit.HEARTS));
        assertEquals(11, result.getCardCountBySuit().get(Suit.SPADES));
        assertEquals(9, result.getCardCountBySuit().get(Suit.CLUBS));
        assertEquals(7, result.getCardCountBySuit().get(Suit.DIAMONDS));
    }

    @Test
    void toCardCountByValueResponse_WithValidMap_ShouldCreateResponse() {
        // Given
        Map<String, Integer> countByValue = new HashMap<>();
        countByValue.put("HEARTS-ACE", 1);
        countByValue.put("SPADES-KING", 1);
        countByValue.put("CLUBS-QUEEN", 1);
        countByValue.put("DIAMONDS-JACK", 1);

        // When
        CardCountByValueResponse result = gameMapper.toCardCountByValueResponse(countByValue);

        // Then
        assertNotNull(result);
        assertEquals(countByValue, result.getCardCountByValue());
        assertEquals(1, result.getCardCountByValue().get("HEARTS-ACE"));
        assertEquals(1, result.getCardCountByValue().get("SPADES-KING"));
        assertEquals(1, result.getCardCountByValue().get("CLUBS-QUEEN"));
        assertEquals(1, result.getCardCountByValue().get("DIAMONDS-JACK"));
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
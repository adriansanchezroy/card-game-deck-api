package com.cardgamedeck.card_game_deck_api.application;

import com.cardgamedeck.card_game_deck_api.application.impl.DeckService;
import com.cardgamedeck.card_game_deck_api.application.impl.GameService;
import com.cardgamedeck.card_game_deck_api.application.impl.PlayerService;
import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.domain.model.Deck;
import com.cardgamedeck.card_game_deck_api.domain.model.Game;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Value;
import com.cardgamedeck.card_game_deck_api.domain.repository.IGameRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private IGameRepository gameRepository;

    @Mock
    private DeckService deckService;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private GameService gameService;

    private UUID gameId;
    private UUID deckId;
    private UUID playerId;
    private Game testGame;
    private Deck testDeck;
    private Player testPlayer;

    @BeforeEach
    void setUp() {
        // Initialize test data
        gameId = UUID.randomUUID();
        deckId = UUID.randomUUID();
        playerId = UUID.randomUUID();

        testGame = new Game("Test Game");
        setPrivateId(testGame, gameId);

        testDeck = new Deck("Test Deck");
        setPrivateId(testDeck, deckId);

        testPlayer = new Player("Test Player");
        setPrivateId(testPlayer, playerId);
    }

    private void setPrivateId(Object entity, UUID id) {
        try {
            java.lang.reflect.Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            fail("Failed to set up test entity ID: " + e.getMessage());
        }
    }

    @Test
    void createGame_WithValidName_ShouldReturnCreatedGame() {
        // Given
        String gameName = "New Test Game";
        Game newGame = new Game(gameName);
        when(gameRepository.save(any(Game.class))).thenReturn(newGame);

        // When
        Game result = gameService.createGame(gameName);

        // Then
        assertNotNull(result);
        assertEquals(gameName, result.getName());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void addDeckToGame_WithValidIds_ShouldAddDeckToGame() {
        // Given
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(deckService.findById(deckId)).thenReturn(Optional.of(testDeck));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // When
        Game result = gameService.addDeckToGame(gameId, deckId);

        // Then
        assertNotNull(result);
        verify(gameRepository, times(1)).findById(gameId);
        verify(deckService, times(1)).findById(deckId);
        verify(gameRepository, times(1)).save(testGame);
    }

    @Test
    void addDeckToGame_WithInvalidGameId_ShouldThrowEntityNotFoundException() {
        // Given
        UUID invalidGameId = UUID.randomUUID();
        when(gameRepository.findById(invalidGameId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                gameService.addDeckToGame(invalidGameId, deckId)
        );
        verify(gameRepository, times(1)).findById(invalidGameId);
        verify(deckService, never()).findById(any(UUID.class));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void addDeckToGame_WithInvalidDeckId_ShouldThrowEntityNotFoundException() {
        // Given
        UUID invalidDeckId = UUID.randomUUID();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(deckService.findById(invalidDeckId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                gameService.addDeckToGame(gameId, invalidDeckId)
        );
        verify(gameRepository, times(1)).findById(gameId);
        verify(deckService, times(1)).findById(invalidDeckId);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void addPlayerToGame_WithValidIds_ShouldAddPlayerToGame() {
        // Given
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(playerService.findById(playerId)).thenReturn(Optional.of(testPlayer));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // When
        Game result = gameService.addPlayerToGame(gameId, playerId);

        // Then
        assertNotNull(result);
        verify(gameRepository, times(1)).findById(gameId);
        verify(playerService, times(1)).findById(playerId);
        verify(gameRepository, times(1)).save(testGame);
    }

    @Test
    void removePlayerFromGame_WithValidIds_ShouldRemovePlayerFromGame() {
        // Given
        testGame.addPlayer(testPlayer);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(playerService.findById(playerId)).thenReturn(Optional.of(testPlayer));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // When
        Game result = gameService.removePlayerFromGame(gameId, playerId);

        // Then
        assertNotNull(result);
        assertFalse(result.getPlayers().contains(testPlayer));
        verify(gameRepository, times(1)).findById(gameId);
        verify(playerService, times(1)).findById(playerId);
        verify(gameRepository, times(1)).save(testGame);
    }

    @Test
    void removePlayerFromGame_WithPlayerNotInGame_ShouldThrowIllegalArgumentException() {
        // Given
        // Player not added to game

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(playerService.findById(playerId)).thenReturn(Optional.of(testPlayer));

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                gameService.removePlayerFromGame(gameId, playerId)
        );
        verify(gameRepository, times(1)).findById(gameId);
        verify(playerService, times(1)).findById(playerId);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void dealCardsToPlayer_WithValidParameters_ShouldDealCardsToPlayer() {
        // Given
        testGame.addPlayer(testPlayer);

        Deck testDeck = new Deck("Test Deck");
        testGame.addDeck(testDeck);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(playerService.findById(playerId)).thenReturn(Optional.of(testPlayer));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // When
        Game result = gameService.dealCardsToPlayer(gameId, playerId, 5);

        // Then
        assertNotNull(result);
        verify(gameRepository, times(1)).findById(gameId);
        verify(playerService, times(1)).findById(playerId);
        verify(gameRepository, times(1)).save(testGame);
    }


    @Test
    void dealCardsToPlayer_WithNegativeCount_ShouldThrowIllegalArgumentException() {
        // Given
        int negativeCount = -1;

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                gameService.dealCardsToPlayer(gameId, playerId, negativeCount)
        );
        verify(gameRepository, never()).findById(any(UUID.class));
        verify(playerService, never()).findById(any(UUID.class));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void dealCardsToPlayer_WithPlayerNotInGame_ShouldThrowIllegalArgumentException() {
        // Given
        // Player not added to game

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(playerService.findById(playerId)).thenReturn(Optional.of(testPlayer));

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                gameService.dealCardsToPlayer(gameId, playerId, 5)
        );
        verify(gameRepository, times(1)).findById(gameId);
        verify(playerService, times(1)).findById(playerId);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void getPlayersWithTotalValues_ShouldReturnPlayersInDescendingOrderByTotalValue() {
        // Create game and players
        Game game = new Game("Test Sorting Game");
        setPrivateId(game, gameId);

        // Create players
        Player player1 = new Player("Player One");
        Player player2 = new Player("Player Two");
        Player player3 = new Player("Player Three");
        setPrivateId(player1, UUID.randomUUID());
        setPrivateId(player2, UUID.randomUUID());
        setPrivateId(player3, UUID.randomUUID());

        // Create cards with unique IDs
        Card card1ForPlayer1 = new Card(Suit.HEARTS, Value.KING); // 13 points
        Card card2ForPlayer1 = new Card(Suit.CLUBS, Value.TWO);   // 2 points
        Card card1ForPlayer2 = new Card(Suit.DIAMONDS, Value.QUEEN); // 12 points
        Card card2ForPlayer2 = new Card(Suit.SPADES, Value.JACK);    // 11 points
        Card card1ForPlayer3 = new Card(Suit.CLUBS, Value.FIVE); // 5 points
        Card card2ForPlayer3 = new Card(Suit.HEARTS, Value.FOUR); // 4 points

        // Assign unique IDs to cards for testing
        setPrivateId(card1ForPlayer1, UUID.randomUUID());
        setPrivateId(card2ForPlayer1, UUID.randomUUID());
        setPrivateId(card1ForPlayer2, UUID.randomUUID());
        setPrivateId(card2ForPlayer2, UUID.randomUUID());
        setPrivateId(card1ForPlayer3, UUID.randomUUID());
        setPrivateId(card2ForPlayer3, UUID.randomUUID());

        // Add cards to players
        player1.addCard(card1ForPlayer1);
        player1.addCard(card2ForPlayer1);
        player2.addCard(card1ForPlayer2);
        player2.addCard(card2ForPlayer2);
        player3.addCard(card1ForPlayer3);
        player3.addCard(card2ForPlayer3);

        // Add players to game
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        // When
        List<Player> result = gameService.getPlayersWithTotalValues(gameId);

        // Then
        assertEquals(3, result.size());
        assertEquals("Player Two", result.get(0).getName());
        assertEquals("Player One", result.get(1).getName());
        assertEquals("Player Three", result.get(2).getName());
    }

    @Test
    void getUndealtCardsBySuit_ShouldReturnMapOfSuitCounts() {
        // Given
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));

        // When
        Map<Suit, Integer> result = gameService.getUndealtCardsBySuit(gameId);

        // Then
        assertNotNull(result);
        verify(gameRepository, times(1)).findById(gameId);
    }

    @Test
    void getUndealtCardsBySuit_ShouldReturnCorrectCountsPerSuit() {
        // Given
        Game mockGame = mock(Game.class);

        // Create expected result
        Map<Suit, Integer> expectedCounts = new HashMap<>();
        expectedCounts.put(Suit.HEARTS, 0);    // 0 hearts undealt
        expectedCounts.put(Suit.SPADES, 8);    // 8 spades undealt
        expectedCounts.put(Suit.CLUBS, 13);    // 13 clubs undealt
        expectedCounts.put(Suit.DIAMONDS, 13); // 13 diamonds undealt

        // Configure mock behavior
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(mockGame));
        when(mockGame.getUndealtCardsBySuit()).thenReturn(expectedCounts);

        // When
        Map<Suit, Integer> result = gameService.getUndealtCardsBySuit(gameId);

        // Then
        assertNotNull(result);
        assertEquals(expectedCounts, result);
        verify(gameRepository, times(1)).findById(gameId);
        verify(mockGame, times(1)).getUndealtCardsBySuit();
    }

    @Test
    void getUndealtCardsBySuitAndValue_ShouldReturnCorrectCountsPerCardType() {
        // Given
        Game mockGame = mock(Game.class);

        // Create expected result
        Map<String, Integer> expectedCounts = new HashMap<>();
        // Add some sample expected values
        expectedCounts.put("HEARTS-ACE", 0);
        expectedCounts.put("SPADES-KING", 0);
        expectedCounts.put("CLUBS-QUEEN", 1);
        expectedCounts.put("DIAMONDS-TWO", 1);

        // Configure mock behavior
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(mockGame));
        when(mockGame.getUndealtCardsBySuitAndValue()).thenReturn(expectedCounts);

        // When
        Map<String, Integer> result = gameService.getUndealtCardsBySuitAndValue(gameId);

        // Then
        assertNotNull(result);
        assertEquals(expectedCounts, result);
        verify(gameRepository, times(1)).findById(gameId);
        verify(mockGame, times(1)).getUndealtCardsBySuitAndValue();
    }

    // TODO: not the most interesting test...
    @Test
    void shuffleGameDeck_ShouldShuffleAndReturnGame() {
        // Given
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // When
        Game result = gameService.shuffleGameDeck(gameId);

        // Then
        assertNotNull(result);
        verify(gameRepository, times(1)).findById(gameId);
        verify(gameRepository, times(1)).save(testGame);
    }
}
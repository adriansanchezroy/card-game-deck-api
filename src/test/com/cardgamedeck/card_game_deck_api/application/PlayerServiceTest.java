package com.cardgamedeck.card_game_deck_api.application;

import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.application.impl.PlayerService;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Value;
import com.cardgamedeck.card_game_deck_api.domain.repository.IPlayerRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private IPlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private UUID playerId;
    private Player testPlayer;
    private Set<Card> testCards;

    @BeforeEach
    void setUp() {
        // Initialize test data
        playerId = UUID.randomUUID();
        testPlayer = new Player("Test Player");
        setPrivateId(testPlayer, playerId);

        // Create test cards
        testCards = new HashSet<>();
        Card card1 = new Card(Suit.HEARTS, Value.ACE);
        Card card2 = new Card(Suit.SPADES, Value.KING);
        setPrivateId(card1, UUID.randomUUID());
        setPrivateId(card2, UUID.randomUUID());

        // Add cards to player
        testPlayer.addCard(card1);
        testPlayer.addCard(card2);
        testCards.add(card1);
        testCards.add(card2);
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
    void createPlayer_WithValidName_ShouldReturnCreatedPlayer() {
        // Given
        String playerName = "New Test Player";
        Player newPlayer = new Player(playerName);
        when(playerRepository.save(any(Player.class))).thenReturn(newPlayer);

        // When
        Player result = playerService.createPlayer(playerName);

        // Then
        assertNotNull(result);
        assertEquals(playerName, result.getName());
        assertEquals(0, result.getCards().size());
        verify(playerRepository, times(1)).save(any(Player.class));
    }

    @Test
    void getPlayerCards_WithExistingPlayer_ShouldReturnPlayerCards() {
        // Given
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));

        // When
        Set<Card> result = playerService.getPlayerCards(playerId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(playerRepository, times(1)).findById(playerId);
    }

    @Test
    void getPlayerCards_WithNonExistingPlayer_ShouldThrowEntityNotFoundException() {
        // Given
        UUID nonExistingId = UUID.randomUUID();
        when(playerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                playerService.getPlayerCards(nonExistingId)
        );
        verify(playerRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void getPlayerTotalValue_WithExistingPlayer_ShouldReturnCorrectTotal() {
        // Given
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));
        int expectedTotal = Value.ACE.getFaceValue() + Value.KING.getFaceValue();

        // When
        int result = playerService.getPlayerTotalValue(playerId);

        // Then
        assertEquals(expectedTotal, result);
        verify(playerRepository, times(1)).findById(playerId);
    }

    @Test
    void getPlayerTotalValue_WithNonExistingPlayer_ShouldThrowEntityNotFoundException() {
        // Given
        UUID nonExistingId = UUID.randomUUID();
        when(playerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                playerService.getPlayerTotalValue(nonExistingId)
        );
        verify(playerRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void clearPlayerCards_WithExistingPlayer_ShouldClearCardsAndReturnPlayer() {
        // Given
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(testPlayer);

        // When
        Player result = playerService.clearPlayerCards(playerId);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getCards().size());
        verify(playerRepository, times(1)).findById(playerId);
        verify(playerRepository, times(1)).save(testPlayer);
    }

    @Test
    void clearPlayerCards_WithNonExistingPlayer_ShouldThrowEntityNotFoundException() {
        // Given
        UUID nonExistingId = UUID.randomUUID();
        when(playerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                playerService.clearPlayerCards(nonExistingId)
        );
        verify(playerRepository, times(1)).findById(nonExistingId);
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void findById_WithExistingPlayer_ShouldReturnPlayer() {
        // Given
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));

        // When
        Optional<Player> result = playerService.findById(playerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(playerId, result.get().getId());
        verify(playerRepository, times(1)).findById(playerId);
    }

    @Test
    void findById_WithNonExistingPlayer_ShouldReturnEmptyOptional() {
        // Given
        UUID nonExistingId = UUID.randomUUID();
        when(playerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // When
        Optional<Player> result = playerService.findById(nonExistingId);

        // Then
        assertFalse(result.isPresent());
        verify(playerRepository, times(1)).findById(nonExistingId);
    }
}
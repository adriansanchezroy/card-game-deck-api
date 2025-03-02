package domain;

import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.domain.model.Deck;
import com.cardgamedeck.card_game_deck_api.domain.model.Game;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Value;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.TestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;
    private Player player;
    private Deck standardDeck;

    @BeforeEach
    void setUp() {
        game = new Game("Test Game");
        player = new Player("Player 1");
        standardDeck = new Deck("Standard");
    }

    @Test
    void gameCreation_ShouldInitializeWithEmptyStateAndCorrectName() {
        assertNotNull(game);
        assertEquals("Test Game", game.getName());
        assertNotNull(game.getGameDeck());
        assertTrue(game.getPlayers().isEmpty());
    }

    @Test
    void addDeck_ShouldPopulateGameDeckWithAllCardsAcrossFourSuits() {
        // Set unique IDs for entities
        TestUtils.setPrivateId(game, UUID.randomUUID());
        TestUtils.setPrivateId(standardDeck, UUID.randomUUID());
        standardDeck.getCards().forEach(card -> TestUtils.setPrivateId(card, UUID.randomUUID()));

        game.addDeck(standardDeck);

        Map<Suit, Integer> cardsBySuit = game.getUndealtCardsBySuit();
        assertEquals(13, cardsBySuit.get(Suit.SPADES));
        assertEquals(13, cardsBySuit.get(Suit.HEARTS));
        assertEquals(13, cardsBySuit.get(Suit.DIAMONDS));
        assertEquals(13, cardsBySuit.get(Suit.CLUBS));
    }

    @Test
    void addPlayer_ShouldIncreasePlayerCountAndContainAddedPlayer() {
        // Set unique IDs for entities
        TestUtils.setPrivateId(game, UUID.randomUUID());
        TestUtils.setPrivateId(player, UUID.randomUUID());

        game.addPlayer(player);

        Set<Player> players = game.getPlayers();
        assertEquals(1, players.size());
        assertTrue(players.contains(player));
    }

    @Test
    void removePlayer_ShouldRemovePlayerAndReturnCards() {
        // Set unique IDs for entities
        TestUtils.setPrivateId(standardDeck, UUID.randomUUID());
        standardDeck.getCards().forEach(card -> TestUtils.setPrivateId(card, UUID.randomUUID()));

        TestUtils.setPrivateId(game, UUID.randomUUID());
        TestUtils.setPrivateId(player, UUID.randomUUID());

        // Add player and deal some cards
        game.addDeck(standardDeck);
        game.addPlayer(player);
        game.dealCards(player, 5);

        // Verify cards were dealt
        assertEquals(5, player.getCards().size());

        // Remove player
        game.removePlayer(player);

        // Verify player was removed and cards returned
        assertFalse(game.getPlayers().contains(player));
        assertTrue(player.getCards().isEmpty());
    }

    @Test
    void dealCards_ShouldDealCorrectNumberOfCards() {
        // Create a standard deck
        Deck standardDeck = new Deck("Standard Deck");

        // Ensure unique IDs for cards in the deck
        standardDeck.getCards().forEach(card -> {
            TestUtils.setPrivateId(card, UUID.randomUUID());
        });

        // Create a game and add the deck
        Game game = new Game("Test Game");
        TestUtils.setPrivateId(game, UUID.randomUUID());
        game.addDeck(standardDeck);

        // Create a player and add to game
        Player player1 = new Player("Player 1");
        TestUtils.setPrivateId(player1, UUID.randomUUID());
        game.addPlayer(player1);

        // Deal cards
        game.dealCards(player1, 7);

        // Verify
        assertEquals(7, player1.getCards().size());

        // Optional: Verify deck state
        assertEquals(standardDeck.getCards().size() - 7, game.getGameDeck().getUndealtCount());
    }

    @Test
    void dealCards_WhenPlayerNotInGame_ShouldThrowIllegalArgumentException() {
        // Set unique IDs for entities
        TestUtils.setPrivateId(standardDeck, UUID.randomUUID());
        standardDeck.getCards().forEach(card -> TestUtils.setPrivateId(card, UUID.randomUUID()));

        TestUtils.setPrivateId(game, UUID.randomUUID());
        TestUtils.setPrivateId(player, UUID.randomUUID());

        game.addDeck(standardDeck);

        // Player not added to game
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            game.dealCards(player, 5);
        });

        assertTrue(exception.getMessage().contains("Player is not in this game"));
    }

    @Test
    void getUndealtCardsBySuit_ShouldReturnCorrectCountAfterDealing() {
        // Set unique IDs for entities
        TestUtils.setPrivateId(standardDeck, UUID.randomUUID());
        standardDeck.getCards().forEach(card -> TestUtils.setPrivateId(card, UUID.randomUUID()));

        TestUtils.setPrivateId(game, UUID.randomUUID());
        TestUtils.setPrivateId(player, UUID.randomUUID());

        // Add deck to game
        game.addDeck(standardDeck);

        // Deal some cards
        game.addPlayer(player);
        game.dealCards(player, 10);

        // Check undealt count by suit
        Map<Suit, Integer> undealtBySuit = game.getUndealtCardsBySuit();
        assertEquals(52 - 10, undealtBySuit.values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void getPlayersWithTotalValue_ShouldReturnPlayersSortedByDescendingCardValue() {
        // Create a custom method for deck to support testing
        Deck valueDeck = new Deck("ValueTest");
        // Clear the default cards and add specific test cards
        List<Card> specificCards = Arrays.asList(
                new Card(Suit.SPADES, Value.ACE),   // 1
                new Card(Suit.HEARTS, Value.KING),  // 13
                new Card(Suit.DIAMONDS, Value.FIVE), // 5
                new Card(Suit.CLUBS, Value.TEN)     // 10
        );

        // Add method to Deck class: clearAndAddSpecificCards
        // For test purposes only
        valueDeck.getCards().clear();
        specificCards.forEach(card -> valueDeck.getCards().add(card));

        game.addDeck(valueDeck);

        // Create players with specific UUIDs for testing
        Player playerA = Mockito.mock(Player.class);
        Player playerB = Mockito.mock(Player.class);

        // Set IDs and names for the mocked players
        UUID playerAId = UUID.randomUUID();
        UUID playerBId = UUID.randomUUID();

        Mockito.when(playerA.getId()).thenReturn(playerAId);
        Mockito.when(playerB.getId()).thenReturn(playerBId);
        Mockito.when(playerA.getName()).thenReturn("Player A");
        Mockito.when(playerB.getName()).thenReturn("Player B");

        // Set total values for each player
        Mockito.when(playerA.getTotalValue()).thenReturn(14); // ACE (1) + KING (13)
        Mockito.when(playerB.getTotalValue()).thenReturn(15); // FIVE (5) + TEN (10)

        game.addPlayer(playerA);
        game.addPlayer(playerB);

        // Get sorted players
        List<Player> sortedPlayers = game.getPlayersWithTotalValue();

        // PlayerB should be first with higher value
        assertEquals(playerB, sortedPlayers.get(0));
        assertEquals(playerA, sortedPlayers.get(1));
    }
    
}
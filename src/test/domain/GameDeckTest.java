package domain;

import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.domain.model.Deck;
import com.cardgamedeck.card_game_deck_api.domain.model.GameDeck;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Value;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GameDeckTest {

    private GameDeck gameDeck;
    private Deck deck;
    private List<Card> originalOrder;

    @BeforeEach
    void setUp() {
        // Create a deck
        deck = new Deck("Test Deck");

        // Setup game deck with the deck
        gameDeck = new GameDeck(null);
        gameDeck.addDeck(deck);

        // Save the original order of cards
        originalOrder = new ArrayList<>(gameDeck.getCards());

        // Set unique IDs for each card to ensure proper equality checks
        setUniqueIdsForCards(originalOrder);
    }

    @Test
    void addDeck_ShouldAdd52Cards() {
        // Given
        GameDeck emptyGameDeck = new GameDeck(null);

        // When
        emptyGameDeck.addDeck(deck);

        // Then
        assertEquals(52, emptyGameDeck.getCards().size());
    }

    @Test
    void addMultipleDecks_ShouldAddCorrectNumberOfUniqueCards() {
        // Given
        GameDeck multiDeckGameDeck = new GameDeck(null);
        Deck deck2 = new Deck("Test Deck 2");

        // When
        multiDeckGameDeck.addDeck(deck);
        multiDeckGameDeck.addDeck(deck2);

        // Then
        assertEquals(104, multiDeckGameDeck.getCards().size(),
                "GameDeck should contain 104 cards after adding two 52-card decks");

        // Verify unique decks
        List<Card> deck1Cards = deck.getCards();
        List<Card> deck2Cards = deck2.getCards();

        assertEquals(104, multiDeckGameDeck.getCards().size());
        // Ensure cards from different decks are not identical
        for (Card card1 : deck1Cards) {
            for (Card card2 : deck2Cards) {
                assertNotEquals(card1, card2,
                        "Cards from different decks should not be the same object");
            }
        }
    }

    @Test
    void addMultipleDecks_ShouldHaveCorrectSuitDistribution() {
        // Given
        GameDeck multiDeckGameDeck = new GameDeck(null);
        Deck deck2 = new Deck("Test Deck 2");

        // When
        multiDeckGameDeck.addDeck(deck);
        multiDeckGameDeck.addDeck(deck2);

        // Then
        Map<Suit, Integer> undealtCardsBySuit = multiDeckGameDeck.getUndealtCardsBySuit();

        assertEquals(4, undealtCardsBySuit.size(),
                "Should still have 4 suits");
        assertEquals(26, undealtCardsBySuit.get(Suit.HEARTS),
                "Should have 26 undealt Heart cards");
        assertEquals(26, undealtCardsBySuit.get(Suit.SPADES),
                "Should have 26 undealt Spade cards");
        assertEquals(26, undealtCardsBySuit.get(Suit.CLUBS),
                "Should have 26 undealt Club cards");
        assertEquals(26, undealtCardsBySuit.get(Suit.DIAMONDS),
                "Should have 26 undealt Diamond cards");
    }

    @Test
    void dealCard_WithNoCardDealt_ShouldReturnFirstCard() {
        // When
        Card dealtCard = gameDeck.dealCard();

        // Then
        assertNotNull(dealtCard);
        assertEquals(originalOrder.get(0), dealtCard);
        assertEquals(1, gameDeck.getDealtCards().size());
        assertTrue(gameDeck.getDealtCards().contains(dealtCard));
    }

    @Test
    void dealCard_WithAllCardsDealt_ShouldReturnNull() {
        // Deal all cards
        for (int i = 0; i < 52; i++) {
            gameDeck.dealCard();
        }

        // When
        Card dealtCard = gameDeck.dealCard();

        // Then
        assertNull(dealtCard);
        assertEquals(52, gameDeck.getDealtCards().size());
    }

    @Test
    void returnCards_ShouldRemoveCardsFromDealtSet() {
        // Given
        Card card1 = gameDeck.dealCard();
        Card card2 = gameDeck.dealCard();
        Set<Card> cardsToReturn = new HashSet<>();
        cardsToReturn.add(card1);

        // When
        gameDeck.returnCards(cardsToReturn);

        // Then
        assertEquals(1, gameDeck.getDealtCards().size());
        assertFalse(gameDeck.getDealtCards().contains(card1));
        assertTrue(gameDeck.getDealtCards().contains(card2));
    }

    @Test
    void shuffle_ShouldChangeCardOrder() {
        // Given
        List<Card> beforeShuffle = new ArrayList<>(gameDeck.getCards());

        // When
        gameDeck.shuffle();
        List<Card> afterShuffle = gameDeck.getCards();

        // Then
        assertNotEquals(beforeShuffle, afterShuffle);
        assertEquals(beforeShuffle.size(), afterShuffle.size());

        // Check all cards are still present
        assertTrue(afterShuffle.containsAll(beforeShuffle));
        assertTrue(beforeShuffle.containsAll(afterShuffle));
    }

    @Test
    void shuffle_WithSomeDealtCards_ShouldPreserveDealtStatus() {
        // Given
        // Deal some cards
        Card card1 = gameDeck.dealCard();
        Card card2 = gameDeck.dealCard();
        Set<Card> dealtCardsBefore = new HashSet<>(gameDeck.getDealtCards());

        // When
        gameDeck.shuffle();

        // Then
        Set<Card> dealtCardsAfter = gameDeck.getDealtCards();
        assertEquals(dealtCardsBefore.size(), dealtCardsAfter.size());
        assertEquals(dealtCardsBefore, dealtCardsAfter);
    }

    @Test
    void shuffle_WithTwoDecks_ShouldDealAllCardsInRandomOrder() {
        // Given
        GameDeck twoDeckGame = new GameDeck(null);
        Deck firstDeck = new Deck("First Deck");
        Deck secondDeck = new Deck("Second Deck");

        // Set unique IDs for cards in both decks
        firstDeck.getCards().forEach(card -> TestUtils.setPrivateId(card, UUID.randomUUID()));
        secondDeck.getCards().forEach(card -> TestUtils.setPrivateId(card, UUID.randomUUID()));

        // Add two decks to the game deck (104 cards total)
        twoDeckGame.addDeck(firstDeck);
        twoDeckGame.addDeck(secondDeck);

        // Get the original cards for verification
        List<Card> originalCards = new ArrayList<>(twoDeckGame.getCards());
        assertEquals(104, originalCards.size());

        // When
        twoDeckGame.shuffle();

        // Deal all cards
        List<Card> dealtCards = new ArrayList<>();
        Card card;
        int count = 0;

        while ((card = twoDeckGame.dealCard()) != null) {
            dealtCards.add(card);
            count++;
        }

        // Then
        assertEquals(104, count);
        assertEquals(104, dealtCards.size());

        // Verify the 105th attempt returns null
        assertNull(twoDeckGame.dealCard());

        // Verify all original cards were dealt
        assertTrue(dealtCards.containsAll(originalCards));
        assertTrue(originalCards.containsAll(dealtCards));

        // Verify the order is changed
        assertNotEquals(originalCards, dealtCards);
    }

    @Test
    void getUndealtCount_ShouldReturnCorrectCount() {
        // Given
        assertEquals(52, gameDeck.getUndealtCount());

        // When
        gameDeck.dealCard();
        gameDeck.dealCard();

        // Then
        assertEquals(50, gameDeck.getUndealtCount());
    }

    @Test
    void getUndealtCardsBySuit_WithNoCardDealt_ShouldReturn13ForEachSuit() {
        // When
        Map<Suit, Integer> result = gameDeck.getUndealtCardsBySuit();

        // Then
        assertEquals(4, result.size());
        assertEquals(13, result.get(Suit.HEARTS));
        assertEquals(13, result.get(Suit.SPADES));
        assertEquals(13, result.get(Suit.CLUBS));
        assertEquals(13, result.get(Suit.DIAMONDS));
    }

    @Test
    void getUndealtCardsBySuit_WithSomeCardsDealt_ShouldReturnCorrectCounts() {
        // Given
        // Deal all hearts
        for (Card card : originalOrder.stream()
                .filter(c -> c.getSuit() == Suit.HEARTS)
                .collect(Collectors.toList())) {

            dealSpecificCard(card);
        }

        // When
        Map<Suit, Integer> result = gameDeck.getUndealtCardsBySuit();

        // Then
        assertEquals(0, result.get(Suit.HEARTS));
        assertEquals(13, result.get(Suit.SPADES));
        assertEquals(13, result.get(Suit.CLUBS));
        assertEquals(13, result.get(Suit.DIAMONDS));
    }

    @Test
    void getUndealtCardsByValue_ShouldReturnCorrectSuitAndSuitAndValueCounts() {
        // Given
        // Deal some specific cards
        dealSpecificCard(findCard(Suit.HEARTS, Value.ACE));
        dealSpecificCard(findCard(Suit.SPADES, Value.KING));
        dealSpecificCard(findCard(Suit.DIAMONDS, Value.QUEEN));

        // When
        Map<String, Integer> result = gameDeck.getUndealtCardsBySuitAndValue();

        // Then
        assertEquals(49, result.size()); // 52 - 3 dealt cards

        // Verify specific counts
        assertEquals(0, result.getOrDefault("HEARTS-ACE", 0));
        assertEquals(0, result.getOrDefault("SPADES-KING", 0));
        assertEquals(0, result.getOrDefault("DIAMONDS-QUEEN", 0));
        assertEquals(1, result.getOrDefault("CLUBS-ACE", 0));
        assertEquals(1, result.getOrDefault("HEARTS-KING", 0));
    }

    // Helper method to find a specific card
    private Card findCard(Suit suit, Value value) {
        return originalOrder.stream()
                .filter(card -> card.getSuit() == suit && card.getValue() == value)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Card not found: " + value + " of " + suit));
    }

    // Helper methods
    private void dealSpecificCard(Card targetCard) {
        // Get all undealt cards
        List<Card> undealtCards = gameDeck.getUndealtCards();

        // Check if target card is in undealt cards
        if (!undealtCards.contains(targetCard)) {
            throw new IllegalStateException("Target card already dealt or not in deck");
        }

        // Deal cards until we get the target
        Card dealtCard;
        Set<Card> tempDealt = new HashSet<>();

        do {
            dealtCard = gameDeck.dealCard();
            if (!dealtCard.equals(targetCard)) {
                tempDealt.add(dealtCard);
            }
        } while (!dealtCard.equals(targetCard));

        // Return all cards except the target
        gameDeck.returnCards(tempDealt);

        // Shuffle the deck
        gameDeck.shuffle();
    }

    // Helper Methods
    private void setUniqueIdsForCards(List<Card> cards) {
        for (Card card : cards) {
            TestUtils.setPrivateId(card, UUID.randomUUID());
        }
    }

}
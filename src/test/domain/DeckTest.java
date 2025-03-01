package domain;

import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.domain.model.Deck;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Value;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    @Test
    void initialize_ShouldCreateDeckWith52Cards() {
        // Given
        Deck deck = new Deck("Test Deck");

        // When
        List<Card> cards = deck.getCards();

        // Then
        assertEquals(52, cards.size());
    }

    @Test
    void initialize_ShouldCreateDeckWithAllCombinations() {
        // Given
        Deck deck = new Deck("Test Deck");

        // When
        List<Card> cards = deck.getCards();

        // Then
        // Verify we have all combinations of suits and values
        for (Suit suit : Suit.values()) {
            for (Value value : Value.values()) {
                boolean cardFound = cards.stream()
                        .anyMatch(card -> card.getSuit() == suit && card.getValue() == value);
                assertTrue(cardFound, "Card not found: " + value + " of " + suit);
            }
        }
    }

}
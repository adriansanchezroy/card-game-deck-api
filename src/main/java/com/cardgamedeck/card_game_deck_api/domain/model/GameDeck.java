package com.cardgamedeck.card_game_deck_api.domain.model;

import com.cardgamedeck.card_game_deck_api.domain.model.base.BaseEntity;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.*;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "game_decks")
public class GameDeck extends BaseEntity {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "game_deck_cards",
            joinColumns = @JoinColumn(name = "game_deck_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<Card> cards = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "game_deck_dealt_cards",
            joinColumns = @JoinColumn(name = "game_deck_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private Set<Card> dealtCards = new HashSet<>();

    // Required by JPA
    protected GameDeck() {
    }

    public GameDeck(List<Card> initialCards) {
        super();
        if (initialCards != null) {
            this.cards.addAll(initialCards);
        }
    }

    public void addDeck(Deck deck) {
        cards.addAll(deck.getCards());
    }

    public Card dealCard() {
        for (Card card : cards) {
            if (!dealtCards.contains(card)) {
                dealtCards.add(card);
                return card;
            }
        }
        return null; // No cards left to deal
    }

    public void returnCards(Set<Card> cardsToReturn) {
        dealtCards.removeAll(cardsToReturn);
    }

    public void shuffle() {
        // Create a copy of all cards
        List<Card> allCards = new ArrayList<>(cards);

        // Fisher-Yates shuffle algorithm
        Random random = new Random();
        for (int i = allCards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap elements at indices i and j
            Card temp = allCards.get(i);
            allCards.set(i, allCards.get(j));
            allCards.set(j, temp);
        }

        // Replace cards with shuffled order
        cards.clear();
        cards.addAll(allCards);

    }

    public int getUndealtCount() {
        return cards.size() - dealtCards.size();
    }

    public Map<Suit, Integer> getUndealtCardsBySuit() {
        Map<Suit, Integer> countBySuit = new HashMap<>();
        for (Suit suit : Suit.values()) {
            countBySuit.put(suit, 0);
        }

        getUndealtCards().forEach(card -> {
            Suit suit = card.getSuit();
            countBySuit.put(suit, countBySuit.get(suit) + 1);
        });

        return countBySuit;
    }

    public Map<String, Integer> getUndealtCardsBySuitAndValue() {
        Map<String, Integer> countBySuitAndValue = new HashMap<>();

        getUndealtCards().forEach(card -> {
            String key = card.getSuit() + "-" + card.getValue();
            countBySuitAndValue.put(key, countBySuitAndValue.getOrDefault(key, 0) + 1);
        });

        return countBySuitAndValue;
    }

    public List<Card> getUndealtCards() {
        return cards.stream()
                .filter(card -> !dealtCards.contains(card))
                .collect(Collectors.toList());
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public Set<Card> getDealtCards() {
        return new HashSet<>(dealtCards);
    }
}
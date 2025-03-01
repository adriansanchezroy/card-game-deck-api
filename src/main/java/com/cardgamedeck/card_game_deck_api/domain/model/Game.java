package com.cardgamedeck.card_game_deck_api.domain.model;

import com.cardgamedeck.card_game_deck_api.domain.model.base.BaseEntity;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "games")
public class Game extends BaseEntity {

    @Setter
    @Getter
    @Column(name = "name", nullable = false)
    private String name;

    @Getter
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_deck_id")
    private GameDeck gameDeck;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "game_players",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player> players = new HashSet<>();

    // Required by JPA
    protected Game() {
        this.gameDeck = new GameDeck(null);
    }

    public Game(String name) {
        super();
        this.name = name;
        this.gameDeck = new GameDeck(null);
    }

    public void addDeck(Deck deck) {
        gameDeck.addDeck(deck);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        if (players.remove(player)) {
            Set<Card> playerCards = player.getCards();
            gameDeck.returnCards(playerCards);
            player.removeAllCards();
        }
    }

    public void dealCards(Player player, int count) {
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player is not in this game");
        }

        for (int i = 0; i < count; i++) {
            Card card = gameDeck.dealCard();
            if (card != null) {
                player.addCard(card);
            } else {
                break; // No more cards to deal
            }
        }
    }

    public void shuffleGameDeck() {
        gameDeck.shuffle();
    }

    public Map<Suit, Integer> getUndealtCardsBySuit() {
        return gameDeck.getUndealtCardsBySuit();
    }

    public Map<String, Integer> getUndealtCardsByValue() {
        return gameDeck.getUndealtCardsByValue();
    }

    public List<Player> getPlayersWithTotalValue() {
        return players.stream()
                .sorted(Comparator.comparingInt(Player::getTotalValue).reversed())
                .collect(Collectors.toList());
    }

    public Set<Player> getPlayers() {
        return new HashSet<>(players);
    }

}

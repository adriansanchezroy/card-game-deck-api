package com.cardgamedeck.card_game_deck_api.domain.model;

import com.cardgamedeck.card_game_deck_api.domain.model.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "players")
public class Player extends BaseEntity {

    @Setter
    @Getter
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "player_cards",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private Set<Card> cards = new HashSet<>();

    // Required by JPA
    protected Player() {
    }

    public Player(String name) {
        super();
        this.name = name;
    }

    public Set<Card> getCards() {
        return new HashSet<>(cards);
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeAllCards() {
        cards.clear();
    }

    public int getTotalValue() {
        return cards.stream()
                .mapToInt(Card::getFaceValue)
                .sum();
    }
}
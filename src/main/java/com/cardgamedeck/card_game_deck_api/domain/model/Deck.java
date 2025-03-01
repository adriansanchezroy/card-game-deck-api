package com.cardgamedeck.card_game_deck_api.domain.model;

import com.cardgamedeck.card_game_deck_api.domain.model.base.BaseEntity;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "decks")
public class Deck extends BaseEntity {

    @Getter
    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "deck_cards",
            joinColumns = @JoinColumn(name = "deck_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<Card> cards = new ArrayList<>();

    // Required by JPA
    protected Deck() {
    }

    public Deck(String name) {
        super();
        this.name = name;
        initialize();
    }

    public void initialize() {
        cards.clear();
        for (Suit suit : Suit.values()) {
            for (Value value : Value.values()) {
                cards.add(new Card(suit, value));
            }
        }
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
}

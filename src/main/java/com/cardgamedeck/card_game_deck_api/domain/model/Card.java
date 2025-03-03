package com.cardgamedeck.card_game_deck_api.domain.model;

import com.cardgamedeck.card_game_deck_api.domain.model.base.BaseEntity;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "cards")
public class Card extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "suit", nullable = false)
    private Suit suit;

    @Enumerated(EnumType.STRING)
    @Column(name = "value", nullable = false)
    private Value value;

    // Required by JPA
    protected Card() {
    }

    public Card(Suit suit, Value value) {
        super();
        this.suit = suit;
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    public Value getValue() {
        return value;
    }

    public int getFaceValue() {
        return value.getFaceValue();
    }

    @Override
    public String toString() {
        return value + " of " + suit;
    }
}

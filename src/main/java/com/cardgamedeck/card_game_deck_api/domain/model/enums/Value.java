package com.cardgamedeck.card_game_deck_api.domain.model.enums;

public enum Value {
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13);

    private final int faceValue;

    Value(int faceValue) {
        this.faceValue = faceValue;
    }

    public int getFaceValue() {
        return faceValue;
    }
}
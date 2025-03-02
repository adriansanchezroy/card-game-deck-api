package com.cardgamedeck.card_game_deck_api.presentation.dto;

import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private UUID id;
    private Suit suit;
    private Value value;
    private int faceValue;
}
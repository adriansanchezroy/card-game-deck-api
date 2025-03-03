package com.cardgamedeck.card_game_deck_api.presentation.dto.response;

import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardCountBySuitResponse {
    private Map<Suit, Integer> cardCountBySuit;
}
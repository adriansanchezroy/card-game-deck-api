package com.cardgamedeck.card_game_deck_api.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardCountByValueResponse {
    private Map<String, Integer> cardCountByValue;
}
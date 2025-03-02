package com.cardgamedeck.card_game_deck_api.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealCardsRequest {
    private int count = 1;
}
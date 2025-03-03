package com.cardgamedeck.card_game_deck_api.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerScoreResponse {
    private UUID playerId;
    private String playerName;
    private int totalValue;
    private int cardCount;
}
package com.cardgamedeck.card_game_deck_api.presentation.dto.response;

import com.cardgamedeck.card_game_deck_api.presentation.dto.CardDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCardsResponse {
    private UUID playerId;
    private String playerName;
    private Set<CardDTO> cards;
}
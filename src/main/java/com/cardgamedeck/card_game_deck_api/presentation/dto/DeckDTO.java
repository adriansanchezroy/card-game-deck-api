package com.cardgamedeck.card_game_deck_api.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckDTO {
    private UUID id;
    private String name;
    private int cardCount;
}
package com.cardgamedeck.card_game_deck_api.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealCardsRequest {
    @Min(value = 1, message = "Must deal at least 1 card")
    @Max(value = 52, message = "Cannot deal more than 52 cards at once")

    private int count;
}
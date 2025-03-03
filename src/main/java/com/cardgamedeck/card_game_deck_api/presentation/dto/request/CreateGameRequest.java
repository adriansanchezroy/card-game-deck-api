package com.cardgamedeck.card_game_deck_api.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGameRequest {
    @NotBlank(message = "Game name is required")
    @Size(min = 2, max = 50, message = "Game name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Game name can only contain alphanumeric characters and spaces")

    private String name;
}
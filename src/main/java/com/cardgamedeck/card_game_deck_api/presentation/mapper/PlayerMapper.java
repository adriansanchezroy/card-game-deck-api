package com.cardgamedeck.card_game_deck_api.presentation.mapper;

import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.presentation.dto.CardDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.PlayerDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerCardsResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerScoreResponse;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PlayerMapper {

    public PlayerDTO toDTO(Player player) {
        if (player == null) {
            return null;
        }

        return new PlayerDTO(
                player.getId(),
                player.getName(),
                player.getCards().size()
        );
    }

    public PlayerScoreResponse toScoreDTO(Player player) {
        if (player == null) {
            return null;
        }

        return new PlayerScoreResponse(
                player.getId(),
                player.getName(),
                player.getTotalValue(),
                player.getCards().size()
        );
    }

    public PlayerCardsResponse toCardsResponse(Player player, Set<CardDTO> cardDTOs) {
        if (player == null) {
            return null;
        }

        return new PlayerCardsResponse(
                player.getId(),
                player.getName(),
                cardDTOs
        );
    }
}
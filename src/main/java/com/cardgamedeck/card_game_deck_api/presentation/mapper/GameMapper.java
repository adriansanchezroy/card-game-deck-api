package com.cardgamedeck.card_game_deck_api.presentation.mapper;

import com.cardgamedeck.card_game_deck_api.domain.model.Game;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.presentation.dto.GameDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountBySuitResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountByValueResponse;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GameMapper {

    public GameDTO toDTO(Game game) {
        if (game == null) {
            return null;
        }

        return new GameDTO(
                game.getId(),
                game.getName(),
                game.getGameDeck().getUndealtCount(),
                game.getPlayers().size()
        );
    }

    public CardCountBySuitResponse toCardCountBySuitResponse(Map<Suit, Integer> countBySuit) {
        return new CardCountBySuitResponse(countBySuit);
    }

    public CardCountByValueResponse toCardCountByValueResponse(Map<String, Integer> countByValue) {
        return new CardCountByValueResponse(countByValue);
    }
}
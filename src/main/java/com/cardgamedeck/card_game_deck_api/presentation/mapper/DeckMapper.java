package com.cardgamedeck.card_game_deck_api.presentation.mapper;

import com.cardgamedeck.card_game_deck_api.domain.model.Deck;
import com.cardgamedeck.card_game_deck_api.presentation.dto.DeckDTO;
import org.springframework.stereotype.Component;

@Component
public class DeckMapper {

    public DeckDTO toDTO(Deck deck) {
        if (deck == null) {
            return null;
        }

        return new DeckDTO(
                deck.getId(),
                deck.getName(),
                deck.getCards().size()
        );
    }
}
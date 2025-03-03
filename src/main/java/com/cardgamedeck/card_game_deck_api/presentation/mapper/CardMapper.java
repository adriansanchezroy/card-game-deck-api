package com.cardgamedeck.card_game_deck_api.presentation.mapper;

import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.presentation.dto.CardDTO;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardDTO toDTO(Card card) {
        if (card == null) {
            return null;
        }

        return new CardDTO(
                card.getId(),
                card.getSuit(),
                card.getValue(),
                card.getFaceValue()
        );
    }
}
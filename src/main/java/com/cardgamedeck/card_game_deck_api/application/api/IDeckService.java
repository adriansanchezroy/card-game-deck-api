package com.cardgamedeck.card_game_deck_api.application.api;

import com.cardgamedeck.card_game_deck_api.application.api.base.IBaseService;
import com.cardgamedeck.card_game_deck_api.domain.model.Deck;

import java.util.UUID;

/**
 * Service interface for operations specific to the Deck entity.
 * Extends the BaseService to inherit common CRUD operations.
 */
public interface IDeckService extends IBaseService<Deck> {

    /**
     * Creates a new standard 52-card deck with the given name.
     * This method initializes a deck with all 52 cards (4 suits x 13 values).
     *
     * @param name The name of the deck to create
     * @return The created deck
     */
    Deck createDeck(String name);

    /**
     * Reinitialize an existing deck with a fresh set of 52 cards.
     * This method clears any existing cards and adds all 52 standard cards.
     *
     * @param deckId The ID of the deck to reinitialize
     * @return The reinitialized deck
     * @throws jakarta.persistence.EntityNotFoundException if deck not found
     */
    Deck reinitializeDeck(UUID deckId);
}
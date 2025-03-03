package com.cardgamedeck.card_game_deck_api.application.api;

import com.cardgamedeck.card_game_deck_api.application.api.base.IBaseService;
import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;

import java.util.Set;
import java.util.UUID;

/**
 * Service interface for operations specific to the Player entity.
 * Extends the BaseService to inherit common CRUD operations.
 */
public interface IPlayerService extends IBaseService<Player> {

    /**
     * Creates a new player with the given name.
     *
     * @param name The name of the player to create
     * @return The created player
     */
    Player createPlayer(String name);

    /**
     * Gets the cards currently held by a player.
     *
     * @param playerId The ID of the player
     * @return A set of cards held by the player
     * @throws jakarta.persistence.EntityNotFoundException if player not found
     */
    Set<Card> getPlayerCards(UUID playerId);

    /**
     * Calculates the total value of all cards held by a player.
     *
     * @param playerId The ID of the player
     * @return The total value of all cards
     * @throws jakarta.persistence.EntityNotFoundException if player not found
     */
    int getPlayerTotalValue(UUID playerId);

    /**
     * Removes all cards from a player's hand.
     *
     * @param playerId The ID of the player
     * @return The updated player with no cards
     * @throws jakarta.persistence.EntityNotFoundException if player not found
     */
    Player clearPlayerCards(UUID playerId);
}
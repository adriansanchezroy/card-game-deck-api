package com.cardgamedeck.card_game_deck_api.application.api;

import com.cardgamedeck.card_game_deck_api.application.api.base.IBaseService;
import com.cardgamedeck.card_game_deck_api.domain.model.Game;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for operations specific to the Game entity.
 * Extends the BaseService to inherit common CRUD operations.
 */
public interface IGameService extends IBaseService<Game> {

    /**
     * Creates a new game with the given name.
     *
     * @param name The name of the game to create
     * @return The created game
     */
    Game createGame(String name);

    /**
     * Adds a deck to the game deck (shoe).
     *
     * @param gameId The ID of the game
     * @param deckId The ID of the deck to add
     * @return The updated game
     * @throws jakarta.persistence.EntityNotFoundException if game or deck not found
     */
    Game addDeckToGame(UUID gameId, UUID deckId);

    /**
     * Adds a player to the game.
     *
     * @param gameId The ID of the game
     * @param playerId The ID of the player to add
     * @return The updated game
     * @throws jakarta.persistence.EntityNotFoundException if game or player not found
     */
    Game addPlayerToGame(UUID gameId, UUID playerId);

    /**
     * Removes a player from the game.
     * This also returns the player's cards to the game deck.
     *
     * @param gameId The ID of the game
     * @param playerId The ID of the player to remove
     * @return The updated game
     * @throws jakarta.persistence.EntityNotFoundException if game or player not found
     * @throws IllegalArgumentException if player is not in the game
     */
    Game removePlayerFromGame(UUID gameId, UUID playerId);

    /**
     * Deals cards to a player in the game.
     *
     * @param gameId The ID of the game
     * @param playerId The ID of the player
     * @param count The number of cards to deal
     * @return The updated game
     * @throws jakarta.persistence.EntityNotFoundException if game or player not found
     * @throws IllegalArgumentException if player is not in the game
     */
    Game dealCardsToPlayer(UUID gameId, UUID playerId, int count);

    /**
     * Gets all players in a game with their total card values.
     * The players are sorted in descending order by their total value.
     *
     * @param gameId The ID of the game
     * @return A list of players sorted by total value (descending)
     * @throws jakarta.persistence.EntityNotFoundException if game not found
     */
    List<Player> getPlayersWithTotalValues(UUID gameId);

    /**
     * Gets all players in a game.
     *
     * @param gameId The ID of the game
     * @return A set of players in the game
     * @throws jakarta.persistence.EntityNotFoundException if game not found
     */
    Set<Player> getGamePlayers(UUID gameId);

    /**
     * Gets the count of undealt cards by suit in the game deck.
     *
     * @param gameId The ID of the game
     * @return A map of suits to card counts
     * @throws jakarta.persistence.EntityNotFoundException if game not found
     */
    Map<Suit, Integer> getUndealtCardsBySuit(UUID gameId);

    /**
     * Gets the count of undealt cards by card value and suit in the game deck.
     *
     * @param gameId The ID of the game
     * @return A map of card identifiers to card counts
     * @throws jakarta.persistence.EntityNotFoundException if game not found
     */
    Map<String, Integer> getUndealtCardsByValue(UUID gameId);

    /**
     * Shuffles the game deck (shoe).
     *
     * @param gameId The ID of the game
     * @return The updated game
     * @throws jakarta.persistence.EntityNotFoundException if game not found
     */
    Game shuffleGameDeck(UUID gameId);
}
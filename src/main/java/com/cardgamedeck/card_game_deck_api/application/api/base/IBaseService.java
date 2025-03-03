package com.cardgamedeck.card_game_deck_api.application.api.base;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Generic base service interface that defines common operations for all entities.
 * This follows the service layer pattern to provide a common abstraction for
 * basic CRUD operations.
 *
 * @param <T> The entity type this service will operate on
 */
public interface IBaseService<T> {

    /**
     * Save a new entity or update an existing one
     *
     * @param entity The entity to save
     * @return The saved entity with generated ID (if new)
     */
    T save(T entity);

    /**
     * Find an entity by its ID
     *
     * @param id The ID of the entity to find
     * @return An Optional containing the entity if found, empty otherwise
     */
    Optional<T> findById(UUID id);

    /**
     * Get all entities of this type
     *
     * @return A list of all entities
     */
    List<T> findAll();

    /**
     * Delete an entity by its ID
     *
     * @param id The ID of the entity to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean deleteById(UUID id);

    /**
     * Check if an entity with the given ID exists
     *
     * @param id The ID to check
     * @return true if entity exists, false otherwise
     */
    boolean existsById(UUID id);
}
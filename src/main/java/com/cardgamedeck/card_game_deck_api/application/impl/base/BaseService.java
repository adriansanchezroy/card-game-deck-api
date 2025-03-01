package com.cardgamedeck.card_game_deck_api.application.impl.base;

import com.cardgamedeck.card_game_deck_api.application.api.base.IBaseService;
import com.cardgamedeck.card_game_deck_api.domain.model.base.BaseEntity;
import com.cardgamedeck.card_game_deck_api.domain.repository.base.IBaseRepository;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the BaseService interface.
 * Provides common CRUD functionality for all entities.
 *
 * @param <T> The entity type this service will operate on
 * @param <R> The repository type for the entity
 */
public abstract class BaseService<T extends BaseEntity, R extends IBaseRepository<T>> implements IBaseService<T> {

    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public T save(T entity) {
        if (entity.getId() != null) {
            // If entity has an ID, it's an update operation
            entity.setUpdatedAt(LocalDateTime.now());
        }
        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public boolean deleteById(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }
}
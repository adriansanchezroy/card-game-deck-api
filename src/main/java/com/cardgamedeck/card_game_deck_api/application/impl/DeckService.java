package com.cardgamedeck.card_game_deck_api.application.impl;

import com.cardgamedeck.card_game_deck_api.application.api.IDeckService;
import com.cardgamedeck.card_game_deck_api.application.impl.base.BaseService;
import com.cardgamedeck.card_game_deck_api.domain.model.Deck;
import com.cardgamedeck.card_game_deck_api.domain.model.Game;
import com.cardgamedeck.card_game_deck_api.domain.repository.IDeckRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeckService extends BaseService<Deck, IDeckRepository> implements IDeckService {

    public DeckService(IDeckRepository deckRepository) {
        super(deckRepository);
    }

    @Override
    @Transactional
    public Deck createDeck(String name) {
        Deck deck = new Deck(name);

        return repository.save(deck);
    }

    @Override
    @Transactional
    public void deleteDeck(UUID deckId) {
        Deck deck = repository.findById(deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found with ID: " + deckId));

        repository.delete(deck);
    }

    @Override
    @Transactional
    public Deck reinitializeDeck(UUID deckId) {
        Deck deck = repository.findById(deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found with ID: " + deckId));

        // Reinitialize the deck with a fresh set of cards
        deck.initialize();
        deck.setUpdatedAt(LocalDateTime.now());

        return repository.save(deck);
    }
}
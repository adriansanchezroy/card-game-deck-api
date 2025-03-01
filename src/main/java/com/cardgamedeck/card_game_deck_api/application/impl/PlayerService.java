package com.cardgamedeck.card_game_deck_api.application.impl;

import com.cardgamedeck.card_game_deck_api.application.api.IPlayerService;
import com.cardgamedeck.card_game_deck_api.application.impl.base.BaseService;
import com.cardgamedeck.card_game_deck_api.domain.model.Card;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.domain.repository.IPlayerRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class PlayerService extends BaseService<Player, IPlayerRepository> implements IPlayerService {

    public PlayerService(IPlayerRepository playerRepository) {
        super(playerRepository);
    }

    @Override
    @Transactional
    public Player createPlayer(String name) {
        Player player = new Player(name);
        return repository.save(player);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Card> getPlayerCards(UUID playerId) {
        Player player = repository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        return player.getCards();
    }

    @Override
    @Transactional(readOnly = true)
    public int getPlayerTotalValue(UUID playerId) {
        Player player = repository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        return player.getTotalValue();
    }

    @Override
    @Transactional
    public Player clearPlayerCards(UUID playerId) {
        Player player = repository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        player.removeAllCards();
        player.setUpdatedAt(LocalDateTime.now());

        return repository.save(player);
    }
}
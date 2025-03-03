package com.cardgamedeck.card_game_deck_api.application.impl;

import com.cardgamedeck.card_game_deck_api.application.api.IDeckService;
import com.cardgamedeck.card_game_deck_api.application.api.IGameService;
import com.cardgamedeck.card_game_deck_api.application.api.IPlayerService;
import com.cardgamedeck.card_game_deck_api.application.impl.base.BaseService;
import com.cardgamedeck.card_game_deck_api.domain.model.Deck;
import com.cardgamedeck.card_game_deck_api.domain.model.Game;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.domain.model.enums.Suit;
import com.cardgamedeck.card_game_deck_api.domain.repository.IGameRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class GameService extends BaseService<Game, IGameRepository> implements IGameService {

    private final IDeckService deckService;
    private final IPlayerService playerService;

    public GameService(IGameRepository gameRepository,
                       DeckService deckService,
                       PlayerService playerService) {
        super(gameRepository);
        this.deckService = deckService;
        this.playerService = playerService;
    }

    @Override
    @Transactional
    public Game createGame(String name) {
        Game game = new Game(name);
        return repository.save(game);
    }

    @Override
    @Transactional
    public void deleteGame(UUID gameId) {
        Game game = repository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        // Clear all players' cards before deleting the game
        game.getPlayers().forEach(player -> playerService.clearPlayerCards(player.getId()));

        // Remove all players from the game to ensure no references remain
        game.getPlayers().clear();

        // Delete the game from the repository
        repository.delete(game);
    }

    @Override
    @Transactional
    public Game addDeckToGame(UUID gameId, UUID deckId) {
        Game game = repository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        Deck deck = deckService.findById(deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found with ID: " + deckId));

        game.addDeck(deck);
        game.shuffleGameDeck();
        game.setUpdatedAt(LocalDateTime.now());

        return repository.save(game);
    }

    @Override
    @Transactional
    public Game addPlayerToGame(UUID gameId, UUID playerId) {
        Game game = repository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        Player player = playerService.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        if (game.getPlayers().contains(player)) {
            return game;
        }

        if (!player.getCards().isEmpty()) {
            playerService.clearPlayerCards(playerId);
        }

        game.addPlayer(player);
        game.setUpdatedAt(LocalDateTime.now());

        return repository.save(game);
    }

    @Override
    @Transactional
    public Game removePlayerFromGame(UUID gameId, UUID playerId) {
        Game game = repository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        Player player = playerService.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        if (!game.getPlayers().contains(player)) {
            throw new IllegalArgumentException("Player is not in this game");
        }

        if (!player.getCards().isEmpty()) {
            playerService.clearPlayerCards(playerId);
        }

        game.removePlayer(player);
        game.setUpdatedAt(LocalDateTime.now());

        return repository.save(game);
    }

    @Override
    @Transactional
    public Game dealCardsToPlayer(UUID gameId, UUID playerId, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be greater than zero");
        }

        Game game = repository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        Player player = playerService.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        if (!game.getPlayers().contains(player)) {
            throw new IllegalArgumentException("Player is not in this game");
        }

        // Check if there are enough undealt cards
        int undealtCount = game.getGameDeck().getUndealtCount();
        if (undealtCount < count) {
            throw new IllegalArgumentException("Not enough cards left in the deck. Requested: " + count + ", Available: " + undealtCount);
        }

        game.dealCards(player, count);
        game.setUpdatedAt(LocalDateTime.now());

        return repository.save(game);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Player> getPlayersWithTotalValues(UUID gameId) {
        Game game = repository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        return game.getPlayersWithTotalValue();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Player> getGamePlayers(UUID gameId) {
        Game game = repository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        return game.getPlayers();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Suit, Integer> getUndealtCardsBySuit(UUID gameId) {
        Game game = repository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        return game.getUndealtCardsBySuit();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getUndealtCardsBySuitAndValue(UUID gameId) {
        Game game = repository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        return game.getUndealtCardsBySuitAndValue();
    }

    @Override
    @Transactional
    public Game shuffleGameDeck(UUID gameId) {
        Game game = repository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with ID: " + gameId));

        game.shuffleGameDeck();
        game.setUpdatedAt(LocalDateTime.now());

        return repository.save(game);
    }
}
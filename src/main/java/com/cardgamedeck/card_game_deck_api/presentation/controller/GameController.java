package com.cardgamedeck.card_game_deck_api.presentation.controller;

import com.cardgamedeck.card_game_deck_api.application.api.IGameService;
import com.cardgamedeck.card_game_deck_api.domain.model.Game;
import com.cardgamedeck.card_game_deck_api.presentation.dto.GameDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.request.*;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountBySuitResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountByValueResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerScoreResponse;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.GameMapper;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.PlayerMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/games")
@Tag(name = "Game", description = "Game management operations")
public class GameController {

    private final IGameService gameService;
    private final GameMapper gameMapper;
    private final PlayerMapper playerMapper;

    public GameController(IGameService gameService, GameMapper gameMapper, PlayerMapper playerMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
        this.playerMapper = playerMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new game", description = "Creates a new card game")
    public ResponseEntity<GameDTO> createGame(@Valid @RequestBody CreateGameRequest createGameRequest) {
        Game gameEntity = gameService.createGame(createGameRequest.getName());
        GameDTO gameDTO = gameMapper.toDTO(gameEntity);

        return new ResponseEntity<>(gameDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{gameId}")
    @Operation(summary = "Delete a game", description = "Deletes a game by its ID")
    // TODO: When a game is deleted, should reset involved player hand count -> to modify in deleteGame service method
    public ResponseEntity<Void> deleteGame(@PathVariable UUID gameId) {
        gameService.deleteGame(gameId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Get a game by ID", description = "Returns a game by its ID")
    public ResponseEntity<GameDTO> getGameById(@PathVariable UUID gameId) {
        Optional<Game> gameEntity = gameService.findById(gameId);

        return gameEntity.map(game -> new ResponseEntity<>(gameMapper.toDTO(game), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @Operation(summary = "Get all games", description = "Returns all games")
    public ResponseEntity<List<GameDTO>> getAllGames() {
        List<Game> games = gameService.findAll();
        List<GameDTO> gameDTOs = games.stream()
                .map(gameMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(gameDTOs, HttpStatus.OK);
    }

    @PostMapping("/{gameId}/decks/{deckId}")
    @Operation(summary = "Add a deck to a game", description = "Adds a deck to the game's deck (shoe)")
    public ResponseEntity<GameDTO> addDeckToGame(@PathVariable UUID gameId, @PathVariable UUID deckId) {
        Game gameEntity = gameService.addDeckToGame(gameId, deckId);
        GameDTO gameDTO = gameMapper.toDTO(gameEntity);

        return new ResponseEntity<>(gameDTO, HttpStatus.OK);
    }

    @PostMapping("/{gameId}/players/{playerId}")
    @Operation(summary = "Add a player to a game", description = "Adds a player to a game")
    public ResponseEntity<GameDTO> addPlayerToGame(@PathVariable UUID gameId, @PathVariable UUID playerId) {
        Game gameEntity = gameService.addPlayerToGame(gameId, playerId);
        GameDTO gameDTO = gameMapper.toDTO(gameEntity);

        return new ResponseEntity<>(gameDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{gameId}/players/{playerId}")
    @Operation(summary = "Remove a player from a game", description = "Removes a player from a game and returns their cards")
    public ResponseEntity<GameDTO> removePlayerFromGame(@PathVariable UUID gameId, @PathVariable UUID playerId) {
        Game gameEntity = gameService.removePlayerFromGame(gameId, playerId);
        GameDTO gameDTO = gameMapper.toDTO(gameEntity);

        return new ResponseEntity<>(gameDTO, HttpStatus.OK);
    }

    @PostMapping("/{gameId}/players/{playerId}/deal")
    @Operation(summary = "Deal cards to a player", description = "Deals a specified number of cards to a player")
    public ResponseEntity<GameDTO> dealCardsToPlayer(
            @PathVariable UUID gameId,
            @PathVariable UUID playerId,
            @Valid @RequestBody(required = false) DealCardsRequest request) {

        int count = (request != null) ? request.getCount() : 1;
        Game gameEntity = gameService.dealCardsToPlayer(gameId, playerId, count);
        GameDTO gameDTO = gameMapper.toDTO(gameEntity);

        return new ResponseEntity<>(gameDTO, HttpStatus.OK);
    }

    @GetMapping("/{gameId}/players/scores")
    @Operation(summary = "Get player scores", description = "Returns players sorted by their total card values")
    public ResponseEntity<List<PlayerScoreResponse>> getPlayerScores(@PathVariable UUID gameId) {
        List<PlayerScoreResponse> playerScores = gameService.getPlayersWithTotalValues(gameId).stream()
                .map(playerMapper::toScoreDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(playerScores, HttpStatus.OK);
    }

    @GetMapping("/{gameId}/deck/cards-by-suit")
    @Operation(summary = "Get undealt cards by suit", description = "Returns the count of undealt cards grouped by suit")
    public ResponseEntity<CardCountBySuitResponse> getUndealtCardsBySuit(@PathVariable UUID gameId) {
        var cardCount = gameService.getUndealtCardsBySuit(gameId);
        CardCountBySuitResponse response = gameMapper.toCardCountBySuitResponse(cardCount);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{gameId}/deck/cards-by-value")
    @Operation(summary = "Get undealt cards by suit and value", description = "Returns the count of each undealt card by suit and value")
    public ResponseEntity<CardCountByValueResponse> getUndealtCardsByValue(@PathVariable UUID gameId) {
        var cardCount = gameService.getUndealtCardsBySuitAndValue(gameId);
        CardCountByValueResponse response = gameMapper.toCardCountByValueResponse(cardCount);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{gameId}/deck/shuffle")
    @Operation(summary = "Shuffle the game deck", description = "Shuffles all cards in the game deck")
    public ResponseEntity<GameDTO> shuffleGameDeck(@PathVariable UUID gameId) {
        Game gameEntity = gameService.shuffleGameDeck(gameId);
        GameDTO gameDTO = gameMapper.toDTO(gameEntity);

        return new ResponseEntity<>(gameDTO, HttpStatus.OK);
    }
}
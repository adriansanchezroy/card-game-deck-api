package com.cardgamedeck.card_game_deck_api.presentation.controller;

import com.cardgamedeck.card_game_deck_api.application.api.IPlayerService;
import com.cardgamedeck.card_game_deck_api.domain.model.Player;
import com.cardgamedeck.card_game_deck_api.presentation.dto.CardDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.PlayerDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerCardsResponse;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.CardMapper;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.PlayerMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/players")
@Tag(name = "Player", description = "Player management operations")
public class PlayerController {

    private final IPlayerService playerService;
    private final PlayerMapper playerMapper;
    private final CardMapper cardMapper;

    public PlayerController(IPlayerService playerService, PlayerMapper playerMapper, CardMapper cardMapper) {
        this.playerService = playerService;
        this.playerMapper = playerMapper;
        this.cardMapper = cardMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new player", description = "Creates a new player")
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody PlayerDTO playerRequest) {
        Player player = playerService.createPlayer(playerRequest.getName());
        return new ResponseEntity<>(playerMapper.toDTO(player), HttpStatus.CREATED);
    }

    @GetMapping("/{playerId}")
    @Operation(summary = "Get a player by ID", description = "Returns a player by their ID")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable UUID playerId) {
        return playerService.findById(playerId)
                .map(player -> new ResponseEntity<>(playerMapper.toDTO(player), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @Operation(summary = "Get all players", description = "Returns all players")
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        List<PlayerDTO> players = playerService.findAll().stream()
                .map(playerMapper::toDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @GetMapping("/{playerId}/cards")
    @Operation(summary = "Get player's cards", description = "Returns all cards held by a player")
    public ResponseEntity<PlayerCardsResponse> getPlayerCards(@PathVariable UUID playerId) {
        Player player = playerService.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with ID: " + playerId));

        Set<CardDTO> cardDTOs = playerService.getPlayerCards(playerId).stream()
                .map(cardMapper::toDTO)
                .collect(Collectors.toSet());

        PlayerCardsResponse response = playerMapper.toCardsResponse(player, cardDTOs);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
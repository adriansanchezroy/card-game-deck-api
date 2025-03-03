package com.cardgamedeck.card_game_deck_api.presentation.controller;

import com.cardgamedeck.card_game_deck_api.application.api.IDeckService;
import com.cardgamedeck.card_game_deck_api.domain.model.Deck;
import com.cardgamedeck.card_game_deck_api.presentation.dto.DeckDTO;
import com.cardgamedeck.card_game_deck_api.presentation.mapper.DeckMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/decks")
@Tag(name = "Deck", description = "Deck management operations")
public class DeckController {

    private final IDeckService deckService;
    private final DeckMapper deckMapper;

    public DeckController(IDeckService deckService, DeckMapper deckMapper) {
        this.deckService = deckService;
        this.deckMapper = deckMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new deck", description = "Creates a new standard 52-card deck")
    public ResponseEntity<DeckDTO> createDeck(@RequestBody DeckDTO deckRequest) {
        Deck deck = deckService.createDeck(deckRequest.getName());
        DeckDTO deckDTO = deckMapper.toDTO(deck);

        return new ResponseEntity<>(deckDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{deckId}")
    @Operation(summary = "Get a deck by ID", description = "Returns a deck by its ID")
    public ResponseEntity<DeckDTO> getDeckById(@PathVariable UUID deckId) {
        Optional<Deck> deck = deckService.findById(deckId);

        return deck.map(value -> new ResponseEntity<>(deckMapper.toDTO(value), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @Operation(summary = "Get all decks", description = "Returns all decks")
    public ResponseEntity<List<DeckDTO>> getAllDecks() {
        List<Deck> decks = deckService.findAll();

        List<DeckDTO> deckDTOs = decks.stream()
                .map(deckMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(deckDTOs, HttpStatus.OK);
    }
}
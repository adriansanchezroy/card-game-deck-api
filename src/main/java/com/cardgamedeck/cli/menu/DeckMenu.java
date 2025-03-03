package com.cardgamedeck.cli.menu;

import com.cardgamedeck.cli.service.DeckApiService;
import com.cardgamedeck.card_game_deck_api.presentation.dto.DeckDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Component
public class DeckMenu {

    private final DeckApiService deckApiService;

    // For maintaining current state/context
    private UUID currentDeckId;

    public DeckMenu(DeckApiService deckApiService) {
        this.deckApiService = deckApiService;
    }

    public boolean display(Scanner scanner) {
        System.out.println("\nDECK OPERATIONS MENU");
        System.out.println("Current Deck: " + (currentDeckId != null ? currentDeckId : "None selected"));
        System.out.println("1. List All Decks");
        System.out.println("2. Create New Deck");
        System.out.println("3. Select Deck by ID");
        System.out.println("4. View Deck Details");
        System.out.println("0. Back to Main Menu");

        System.out.print("\nEnter your choice: ");
        String input = scanner.nextLine().trim();

        switch (input) {
            case "0":
                return true; // Return to main menu
            case "1":
                listAllDecks();
                break;
            case "2":
                createNewDeck(scanner);
                break;
            case "3":
                selectDeckById(scanner);
                break;
            case "4":
                viewDeckDetails();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }

        // Pause to let user read output
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();

        return true; // Stay in the deck menu
    }

    private void listAllDecks() {
        System.out.println("\nListing all decks:");
        List<DeckDTO> decks = deckApiService.getAllDecks();

        if (decks == null || decks.isEmpty()) {
            System.out.println("No decks found.");
            return;
        }

        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-40s | %-20s | %-10s\n", "ID", "Name", "Cards");
        System.out.println("---------------------------------------------------------------");

        for (DeckDTO deck : decks) {
            System.out.printf("%-40s | %-20s | %-10d\n",
                    deck.getId(),
                    deck.getName(),
                    deck.getCardCount());
        }
    }

    private void createNewDeck(Scanner scanner) {
        System.out.print("\nEnter deck name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Deck name cannot be empty. Operation cancelled.");
            return;
        }

        DeckDTO newDeck = deckApiService.createDeck(name);

        if (newDeck != null) {
            System.out.println("Deck created successfully with ID: " + newDeck.getId());
            currentDeckId = newDeck.getId();
        } else {
            System.out.println("Failed to create deck.");
        }
    }

    private void selectDeckById(Scanner scanner) {
        listAllDecks();

        System.out.print("\nEnter deck ID: ");
        String deckId = scanner.nextLine().trim();

        try {
            UUID id = UUID.fromString(deckId);
            DeckDTO deck = deckApiService.getDeckById(id);

            if (deck != null) {
                currentDeckId = id;
                System.out.println("Selected deck: " + deck.getName() + " (ID: " + deck.getId() + ")");
            } else {
                System.out.println("Deck not found with ID: " + deckId);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
    }

    private void viewDeckDetails() {
        if (currentDeckId == null) {
            System.out.println("No deck currently selected. Please select a deck first.");
            return;
        }

        DeckDTO deck = deckApiService.getDeckById(currentDeckId);

        if (deck == null) {
            System.out.println("Failed to retrieve deck details or deck no longer exists.");
            currentDeckId = null;
            return;
        }

        System.out.println("\nDeck Details:");
        System.out.println("ID: " + deck.getId());
        System.out.println("Name: " + deck.getName());
        System.out.println("Card Count: " + deck.getCardCount());
    }
}
package com.cardgamedeck.cli.menu;

import com.cardgamedeck.cli.service.PlayerApiService;
import com.cardgamedeck.card_game_deck_api.presentation.dto.CardDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.PlayerDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerCardsResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Component
public class PlayerMenu {

    private final PlayerApiService playerApiService;

    // For maintaining current state/context
    private UUID currentPlayerId;

    public PlayerMenu(PlayerApiService playerApiService) {
        this.playerApiService = playerApiService;
    }

    public boolean display(Scanner scanner) {
        System.out.println("\nPLAYER OPERATIONS MENU");
        System.out.println("Current Player: " + (currentPlayerId != null ? currentPlayerId : "None selected"));
        System.out.println("1. List All Players");
        System.out.println("2. Create New Player");
        System.out.println("3. Select Player by ID");
        System.out.println("4. View Player Details");
        System.out.println("5. View Player Cards");
        System.out.println("0. Back to Main Menu");

        System.out.print("\nEnter your choice: ");
        String input = scanner.nextLine().trim();

        switch (input) {
            case "0":
                return true; // Return to main menu
            case "1":
                listAllPlayers();
                break;
            case "2":
                createNewPlayer(scanner);
                break;
            case "3":
                selectPlayerById(scanner);
                break;
            case "4":
                viewPlayerDetails();
                break;
            case "5":
                viewPlayerCards();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }

        // Pause to let user read output
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();

        return true; // Stay in the player menu
    }

    private void listAllPlayers() {
        System.out.println("\nListing all players:");
        List<PlayerDTO> players = playerApiService.getAllPlayers();

        if (players == null || players.isEmpty()) {
            System.out.println("No players found.");
            return;
        }

        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-40s | %-20s | %-10s\n", "ID", "Name", "Cards");
        System.out.println("---------------------------------------------------------------");

        for (PlayerDTO player : players) {
            System.out.printf("%-40s | %-20s | %-10d\n",
                    player.getId(),
                    player.getName(),
                    player.getCardCount());
        }
    }

    private void createNewPlayer(Scanner scanner) {
        System.out.print("\nEnter player name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Player name cannot be empty. Operation cancelled.");
            return;
        }

        PlayerDTO newPlayer = playerApiService.createPlayer(name);

        if (newPlayer != null) {
            System.out.println("Player created successfully with ID: " + newPlayer.getId());
            currentPlayerId = newPlayer.getId();
        } else {
            System.out.println("Failed to create player.");
        }
    }

    private void selectPlayerById(Scanner scanner) {
        System.out.print("\nEnter player ID: ");
        String playerId = scanner.nextLine().trim();

        try {
            UUID id = UUID.fromString(playerId);
            PlayerDTO player = playerApiService.getPlayerById(id);

            if (player != null) {
                currentPlayerId = id;
                System.out.println("Selected player: " + player.getName() + " (ID: " + player.getId() + ")");
            } else {
                System.out.println("Player not found with ID: " + playerId);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
    }

    private void viewPlayerDetails() {
        if (currentPlayerId == null) {
            System.out.println("No player currently selected. Please select a player first.");
            return;
        }

        PlayerDTO player = playerApiService.getPlayerById(currentPlayerId);

        if (player == null) {
            System.out.println("Failed to retrieve player details or player no longer exists.");
            currentPlayerId = null;
            return;
        }

        System.out.println("\nPlayer Details:");
        System.out.println("ID: " + player.getId());
        System.out.println("Name: " + player.getName());
        System.out.println("Card Count: " + player.getCardCount());
    }

    private void viewPlayerCards() {
        if (currentPlayerId == null) {
            System.out.println("No player currently selected. Please select a player first.");
            return;
        }

        PlayerCardsResponse response = playerApiService.getPlayerCards(currentPlayerId);

        if (response == null) {
            System.out.println("Failed to retrieve player cards.");
            return;
        }

        System.out.println("\nCards held by player " + response.getPlayerName() + ":");

        if (response.getCards() == null || response.getCards().isEmpty()) {
            System.out.println("Player has no cards.");
            return;
        }

        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-40s | %-10s | %-10s | %-10s\n", "ID", "Suit", "Value", "Face Value");
        System.out.println("---------------------------------------------------------------");

        for (CardDTO card : response.getCards()) {
            System.out.printf("%-40s | %-10s | %-10s | %-10d\n",
                    card.getId(),
                    card.getSuit(),
                    card.getValue(),
                    card.getFaceValue());
        }
    }

}
package com.cardgamedeck.cli.menu;

import com.cardgamedeck.cli.service.GameApiService;
import com.cardgamedeck.card_game_deck_api.presentation.dto.GameDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountBySuitResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountByValueResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerScoreResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Component
public class GameStatusMenu {

    private final GameApiService gameApiService;

    // For maintaining current state/context
    private UUID currentGameId;

    public GameStatusMenu(GameApiService gameApiService) {
        this.gameApiService = gameApiService;
    }

    public boolean display(Scanner scanner) {
        System.out.println("\nGAME STATUS MENU");
        System.out.println("Current Game: " + (currentGameId != null ? currentGameId : "None selected"));
        System.out.println("1. Select Game by ID");
        System.out.println("2. View Game Details");
        System.out.println("3. View Player Rankings");
        System.out.println("4. View Undealt Cards by Suit");
        System.out.println("5. View Remaining Cards Sorted by Suit and Value");
        System.out.println("0. Back to Main Menu");

        System.out.print("\nEnter your choice: ");
        String input = scanner.nextLine().trim();

        switch (input) {
            case "0":
                return true; // Return to main menu
            case "1":
                selectGameById(scanner);
                break;
            case "2":
                viewGameDetails();
                break;
            case "3":
                viewPlayerRankings();
                break;
            case "4":
                viewUndealtCardsBySuit();
                break;
            case "5":
                viewUndealtCardsByValue();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }

        // Pause to let user read output
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();

        return true; // Stay in the status menu
    }

    private void listAllGames() {
        System.out.println("\nListing all games:");
        List<GameDTO> games = gameApiService.getAllGames();

        if (games == null || games.isEmpty()) {
            System.out.println("No games found.");
            return;
        }

        System.out.println("------------------------------------------------------------------------------------------------");
        System.out.printf("%-40s | %-20s | %-15s | %-10s\n", "ID", "Name", "Undealt Cards", "Players");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (GameDTO game : games) {
            System.out.printf("%-40s | %-20s | %-15d | %-10d\n",
                    game.getId(),
                    game.getName(),
                    game.getUndealtCardCount(),
                    game.getPlayerCount());
        }
    }

    private void selectGameById(Scanner scanner) {
        listAllGames();

        System.out.print("\nEnter game ID: ");
        String gameId = scanner.nextLine().trim();

        try {
            UUID id = UUID.fromString(gameId);
            GameDTO game = gameApiService.getGameById(id);

            if (game != null) {
                currentGameId = id;
                System.out.println("Selected game: " + game.getName() + " (ID: " + game.getId() + ")");
            } else {
                System.out.println("Game not found with ID: " + gameId);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
    }

    private void viewGameDetails() {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        GameDTO game = gameApiService.getGameById(currentGameId);

        if (game == null) {
            System.out.println("Failed to retrieve game details or game no longer exists.");
            currentGameId = null;
            return;
        }

        System.out.println("\nGame Details:");
        System.out.println("ID: " + game.getId());
        System.out.println("Name: " + game.getName());
        System.out.println("Undealt Card Count: " + game.getUndealtCardCount());
        System.out.println("Player Count: " + game.getPlayerCount());
    }

    private void viewPlayerRankings() {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        List<PlayerScoreResponse> players = gameApiService.getPlayerScores(currentGameId);

        if (players == null || players.isEmpty()) {
            System.out.println("No players in the current game.");
            return;
        }

        System.out.println("\nPlayer Rankings (by total card value):");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-5s | %-40s | %-20s | %-10s | %-10s\n", "Rank", "ID", "Name", "Total Value", "Cards");
        System.out.println("---------------------------------------------------------------");

        int rank = 1;
        for (PlayerScoreResponse player : players) {
            System.out.printf("%-5d | %-40s | %-20s | %-10d | %-10d\n",
                    rank++,
                    player.getPlayerId(),
                    player.getPlayerName(),
                    player.getTotalValue(),
                    player.getCardCount());
        }
    }

    private void viewUndealtCardsBySuit() {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        CardCountBySuitResponse response = gameApiService.getUndealtCardsBySuit(currentGameId);

        if (response == null || response.getCardCountBySuit() == null) {
            System.out.println("Failed to get undealt cards by suit.");
            return;
        }

        System.out.println("\nUndealt Cards by Suit:");
        System.out.println("---------------------");
        System.out.printf("%-10s | %-5s\n", "Suit", "Count");
        System.out.println("---------------------");

        response.getCardCountBySuit().forEach((suit, count) ->
                System.out.printf("%-10s | %-5d\n", suit.name(), count)
        );
    }

    private void viewUndealtCardsByValue() {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        CardCountByValueResponse response = gameApiService.getUndealtCardsByValue(currentGameId);

        if (response == null || response.getCardCountByValue() == null) {
            System.out.println("Failed to get undealt cards by value.");
            return;
        }

        System.out.println("\nUndealt Cards by Value:");
        System.out.println("------------------------");
        System.out.printf("%-20s | %-5s\n", "Card", "Count");
        System.out.println("------------------------");

        response.getCardCountByValue().forEach((card, count) -> {
            String[] parts = card.split("-");
            if (parts.length == 2) {
                System.out.printf("%-20s | %-5d\n", parts[1] + " of " + parts[0], count);
            } else {
                System.out.printf("%-20s | %-5d\n", card, count);
            }
        });
    }
}
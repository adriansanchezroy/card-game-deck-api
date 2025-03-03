package com.cardgamedeck.cli.menu;

import com.cardgamedeck.cli.service.GameApiService;
import com.cardgamedeck.cli.service.DeckApiService;
import com.cardgamedeck.cli.service.PlayerApiService;
import com.cardgamedeck.card_game_deck_api.presentation.dto.GameDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.DeckDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.PlayerDTO;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountBySuitResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.CardCountByValueResponse;
import com.cardgamedeck.card_game_deck_api.presentation.dto.response.PlayerScoreResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Component
public class GameMenu {

    private final GameApiService gameApiService;
    private final DeckApiService deckApiService;
    private final PlayerApiService playerApiService;

    // For maintaining current state/context
    private UUID currentGameId;

    public GameMenu(GameApiService gameApiService, DeckApiService deckApiService, PlayerApiService playerApiService) {
        this.gameApiService = gameApiService;
        this.deckApiService = deckApiService;
        this.playerApiService = playerApiService;
    }

    public boolean display(Scanner scanner) {
        System.out.println("\nGAME OPERATIONS MENU");
        System.out.println("Current Game: " + (currentGameId != null ? currentGameId : "None selected"));
        System.out.println("1. List All Games");
        System.out.println("2. Create New Game");
        System.out.println("3. Select Game by ID");
        System.out.println("4. Delete Current Game");
        System.out.println("5. Add Deck to Current Game");
        System.out.println("6. Add Player to Current Game");
        System.out.println("7. Remove Player from Current Game");
        System.out.println("8. Deal Cards to Player");
        System.out.println("9. Shuffle Current Game Deck");
        System.out.println("10. View Player Scores");
        System.out.println("11. View Undealt Cards by Suit");
        System.out.println("12. View Remaining Cards Sorted by Suit and Value");
        System.out.println("0. Back to Main Menu");

        System.out.print("\nEnter your choice: ");
        String input = scanner.nextLine().trim();

        switch (input) {
            case "0":
                return true; // Return to main menu
            case "1":
                listAllGames();
                break;
            case "2":
                createNewGame(scanner);
                break;
            case "3":
                selectGameById(scanner);
                break;
            case "4":
                deleteCurrentGame();
                break;
            case "5":
                addDeckToGame(scanner);
                break;
            case "6":
                addPlayerToGame(scanner);
                break;
            case "7":
                removePlayerFromGame(scanner);
                break;
            case "8":
                dealCardsToPlayer(scanner);
                break;
            case "9":
                shuffleGameDeck();
                break;
            case "10":
                viewPlayerScores();
                break;
            case "11":
                viewUndealtCardsBySuit();
                break;
            case "12":
                viewUndealtCardsByValue();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }

        // Pause to let user read output
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();

        return true; // Stay in the game menu
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

    private void createNewGame(Scanner scanner) {
        System.out.print("\nEnter game name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Game name cannot be empty. Operation cancelled.");
            return;
        }

        GameDTO newGame = gameApiService.createGame(name);

        if (newGame != null) {
            System.out.println("Game created successfully with ID: " + newGame.getId());
            currentGameId = newGame.getId();
        } else {
            System.out.println("Failed to create game.");
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

    private void deleteCurrentGame() {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        System.out.println("Deleting game with ID: " + currentGameId);
        gameApiService.deleteGame(currentGameId);
        currentGameId = null;
        System.out.println("Game deleted successfully.");
    }

    private void addDeckToGame(Scanner scanner) {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        // List available decks
        List<DeckDTO> decks = deckApiService.getAllDecks();

        if (decks == null || decks.isEmpty()) {
            System.out.println("No decks available. Please create a deck first.");
            return;
        }

        System.out.println("\nAvailable Decks:");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-40s | %-20s | %-10s\n", "ID", "Name", "Cards");
        System.out.println("---------------------------------------------------------------");

        for (DeckDTO deck : decks) {
            System.out.printf("%-40s | %-20s | %-10d\n",
                    deck.getId(),
                    deck.getName(),
                    deck.getCardCount());
        }

        System.out.print("\nEnter deck ID to add: ");
        String deckId = scanner.nextLine().trim();

        try {
            UUID id = UUID.fromString(deckId);
            GameDTO updatedGame = gameApiService.addDeckToGame(currentGameId, id);

            if (updatedGame != null) {
                System.out.println("Deck added successfully to game.");
                System.out.println("Game now has " + updatedGame.getUndealtCardCount() + " undealt cards.");
            } else {
                System.out.println("Failed to add deck to game.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
    }

    private void addPlayerToGame(Scanner scanner) {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        // List available players
        List<PlayerDTO> players = playerApiService.getAllPlayers();

        if (players == null || players.isEmpty()) {
            System.out.println("No players available. Please create a player first.");
            return;
        }

        System.out.println("\nAvailable Players:");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-40s | %-20s | %-10s\n", "ID", "Name", "Cards");
        System.out.println("---------------------------------------------------------------");

        for (PlayerDTO player : players) {
            System.out.printf("%-40s | %-20s | %-10d\n",
                    player.getId(),
                    player.getName(),
                    player.getCardCount());
        }

        System.out.print("\nEnter player ID to add: ");
        String playerId = scanner.nextLine().trim();

        try {
            UUID id = UUID.fromString(playerId);
            GameDTO updatedGame = gameApiService.addPlayerToGame(currentGameId, id);

            if (updatedGame != null) {
                System.out.println("Player added successfully to game.");
                System.out.println("Game now has " + updatedGame.getPlayerCount() + " players.");
            } else {
                System.out.println("Failed to add player to game.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
    }

    private void removePlayerFromGame(Scanner scanner) {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        // Get current players in game
        List<PlayerScoreResponse> players = gameApiService.getPlayerScores(currentGameId);

        if (players == null || players.isEmpty()) {
            System.out.println("No players in the current game.");
            return;
        }

        System.out.println("\nPlayers in Current Game:");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-40s | %-20s | %-10s | %-10s\n", "ID", "Name", "Total Value", "Cards");
        System.out.println("---------------------------------------------------------------");

        for (PlayerScoreResponse player : players) {
            System.out.printf("%-40s | %-20s | %-10d | %-10d\n",
                    player.getPlayerId(),
                    player.getPlayerName(),
                    player.getTotalValue(),
                    player.getCardCount());
        }

        System.out.print("\nEnter player ID to remove: ");
        String playerId = scanner.nextLine().trim();

        try {
            UUID id = UUID.fromString(playerId);
            GameDTO updatedGame = gameApiService.removePlayerFromGame(currentGameId, id);

            if (updatedGame != null) {
                System.out.println("Player removed successfully from game.");
                System.out.println("Game now has " + updatedGame.getPlayerCount() + " players.");
            } else {
                System.out.println("Failed to remove player from game.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
    }

    private void dealCardsToPlayer(Scanner scanner) {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        // Get current players in game
        List<PlayerScoreResponse> players = gameApiService.getPlayerScores(currentGameId);

        if (players == null || players.isEmpty()) {
            System.out.println("No players in the current game.");
            return;
        }

        System.out.println("\nPlayers in Current Game:");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-40s | %-20s | %-10s | %-10s\n", "ID", "Name", "Total Value", "Cards");
        System.out.println("---------------------------------------------------------------");

        for (PlayerScoreResponse player : players) {
            System.out.printf("%-40s | %-20s | %-10d | %-10d\n",
                    player.getPlayerId(),
                    player.getPlayerName(),
                    player.getTotalValue(),
                    player.getCardCount());
        }

        System.out.print("\nEnter player ID to deal cards to: ");
        String playerId = scanner.nextLine().trim();

        try {
            UUID id = UUID.fromString(playerId);

            System.out.print("Enter number of cards to deal: ");
            int count;
            try {
                count = Integer.parseInt(scanner.nextLine().trim());
                if (count <= 0) {
                    System.out.println("Number of cards must be positive. Operation cancelled.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Operation cancelled.");
                return;
            }

            GameDTO updatedGame = gameApiService.dealCardsToPlayer(currentGameId, id, count);

            if (updatedGame != null) {
                System.out.println("Cards dealt successfully.");
                System.out.println("Game now has " + updatedGame.getUndealtCardCount() + " undealt cards.");
            } else {
                System.out.println("Failed to deal cards to player.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format.");
        }
    }

    private void shuffleGameDeck() {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        GameDTO updatedGame = gameApiService.shuffleGameDeck(currentGameId);

        if (updatedGame != null) {
            System.out.println("Game deck shuffled successfully.");
        } else {
            System.out.println("Failed to shuffle game deck.");
        }
    }

    private void viewPlayerScores() {
        if (currentGameId == null) {
            System.out.println("No game currently selected. Please select a game first.");
            return;
        }

        List<PlayerScoreResponse> players = gameApiService.getPlayerScores(currentGameId);

        if (players == null || players.isEmpty()) {
            System.out.println("No players in the current game.");
            return;
        }

        System.out.println("\nPlayer Scores (sorted by total value descending):");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-40s | %-20s | %-10s | %-10s\n", "ID", "Name", "Total Value", "Cards");
        System.out.println("---------------------------------------------------------------");

        for (PlayerScoreResponse player : players) {
            System.out.printf("%-40s | %-20s | %-10d | %-10d\n",
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
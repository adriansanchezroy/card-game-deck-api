package com.cardgamedeck.cli.menu;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MainMenu {

    private final GameMenu gameMenu;
    private final DeckMenu deckMenu;
    private final PlayerMenu playerMenu;
    private final GameStatusMenu gameStatusMenu;

    public MainMenu(GameMenu gameMenu, DeckMenu deckMenu, PlayerMenu playerMenu, GameStatusMenu gameStatusMenu) {
        this.gameMenu = gameMenu;
        this.deckMenu = deckMenu;
        this.playerMenu = playerMenu;
        this.gameStatusMenu = gameStatusMenu;
    }

    public boolean display(Scanner scanner) {
        while (true) {  // Keeps CLI running
            System.out.println("\nMAIN MENU");
            System.out.println("1. Game Operations");
            System.out.println("2. Deck Operations");
            System.out.println("3. Player Operations");
            System.out.println("4. Game Status Information");
            System.out.println("0. Exit");
            System.out.print("\nEnter your choice: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    gameMenu.display(scanner);
                    break;
                case "2":
                    deckMenu.display(scanner);
                    break;
                case "3":
                    playerMenu.display(scanner);
                    break;
                case "4":
                    gameStatusMenu.display(scanner);
                    break;
                case "0":
                    System.out.println("Exiting CLI...");
                    return false;  // This should be the only way to exit
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
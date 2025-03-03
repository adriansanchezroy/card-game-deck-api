package com.cardgamedeck.cli;

import com.cardgamedeck.cli.config.CliConfig;
import com.cardgamedeck.cli.menu.MainMenu;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.util.Scanner;

@Profile("cli")
@SpringBootApplication(scanBasePackages = {"com.cardgamedeck.cli"})
public class CardGameDeckCliApplication implements CommandLineRunner {

    private final Scanner scanner;
    private final MainMenu mainMenu;
    private final CliConfig cliConfig;

    public CardGameDeckCliApplication(MainMenu mainMenu, CliConfig cliConfig) {
        this.mainMenu = mainMenu;
        this.cliConfig = cliConfig;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(CardGameDeckCliApplication.class)
                .web(WebApplicationType.NONE)  // Explicitly disable web server
                .run(args);
    }

    @Override
    public void run(String... args) {
        System.out.println("\n================================================");
        System.out.println("          Welcome to Card Game Deck CLI");
        System.out.println("================================================");
        System.out.println("API URL: " + cliConfig.getApiBaseUrl());
        System.out.println("------------------------------------------------\n");

        boolean running = true;
        while (running) {
            try {
                running = mainMenu.display(scanner);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }

        System.out.println("\nThank you for using Card Game CLI. Goodbye!");
        System.exit(0);
    }
}
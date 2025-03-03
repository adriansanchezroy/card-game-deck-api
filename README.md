# Deck of Cards Game API

## Overview

This project is a REST API that simulates a basic deck of standard playing cards along with the services for a game between multiple players. It allows users to create decks, add them to games, manage players, shuffle cards, and deal cards to players. The API follows RESTful best practices and is implemented using **Java**, **Spring Boot**, and **PostgreSQL**, with a CLI component for interacting with the system.

## Features

The API provides the following capabilities:

### Deck Management

- Create a deck
- Get a deck by ID
- Get all decks

### Game Management

- Create a game
- Get a game by ID
- Get all games
- Delete a game
- Add a deck to a game
- Shuffle the game deck
- Get the count of remaining undealt cards (sorted by suit and value)

### Player Management

- Create a player
- Get a player by ID
- Get all players
- Add a player to a game
- Remove a player from a game
- Deal cards to a player
- Get a player's current hand
- Get the total value of a player's hand
- List players in a game sorted by their total hand value

### CLI Menu System

A command-line interface (CLI) is provided to interact with the API. The CLI includes:

- Game Operations
- Deck Operations
- Player Operations
- Game Status Information

## Getting Started

### Prerequisites

Ensure you have the following installed:

- **Docker & Docker Compose** (for PostgreSQL database)
- **Java 17+**
- **Maven**

### Setup & Running the Application

#### Step 1: Clone the Repository

```sh
git clone <repository_url>
cd <repository_folder>
```

#### Step 2: Start the Database

The application uses PostgreSQL as the database. You can start the database container using Docker:

```sh
docker-compose up -d postgres
```

This will start the PostgreSQL database in a detached mode.

#### Step 3: Run the Spring Boot API Locally

Use the following command to run the API locally:

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

This will start the REST API, allowing you to interact with it via HTTP requests.

#### Step 4: Run the CLI Interface

To start the CLI application, in a separate terminal, use the following command:

```sh
./mvnw spring-boot:run -Pcli -Dspring-boot.run.profiles=cli
```

This will launch an interactive command-line interface where you can create games, add players, deal cards, etc.

## API Endpoints

### Deck Endpoints

| Method | Endpoint          | Description       |
| ------ | ----------------- | ----------------- |
| `POST` | `/decks`          | Create a new deck |
| `GET`  | `/decks/{deckId}` | Get a deck by ID  |
| `GET`  | `/decks`          | Get all decks     |

### Game Endpoints

| Method   | Endpoint                                  | Description                  |
| -------- | ----------------------------------------- | ---------------------------- |
| `POST`   | `/games`                                  | Create a new game            |
| `GET`    | `/games/{gameId}`                         | Get a game by ID             |
| `GET`    | `/games`                                  | Get all games                |
| `DELETE` | `/games/{gameId}`                         | Delete a game                |
| `POST`   | `/games/{gameId}/decks/{deckId}`          | Add a deck to a game         |
| `POST`   | `/games/{gameId}/deck/shuffle`            | Shuffle game deck            |
| `GET`    | `/games/{gameId}/deck/cards-by-suit`      | Count undealt cards by suit  |
| `GET`    | `/games/{gameId}/deck/cards-by-value`     | Count undealt cards by value |
| `POST`   | `/games/{gameId}/players/{playerId}`      | Add a player to a game       |
| `DELETE` | `/games/{gameId}/players/{playerId}`      | Remove a player from a game  |
| `POST`   | `/games/{gameId}/players/{playerId}/deal` | Deal cards to a player       |
| `GET`    | `/games/{gameId}/players/scores`          | Get sorted player rankings   |

### Player Endpoints

| Method | Endpoint                    | Description          |
| ------ | --------------------------- | -------------------- |
| `POST` | `/players`                  | Create a player      |
| `GET`  | `/players/{playerId}`       | Get a player by ID   |
| `GET`  | `/players`                  | Get all players      |
| `GET`  | `/players/{playerId}/cards` | Get a player's cards |

## Technologies Used

- **Java 17**
- **Spring Boot**
- **PostgreSQL**
- **Docker & Docker Compose**
- **Maven**

## Dev Diary

During the implementation of this project, I successfully incorporated most of the architectural patterns, components, and principles I was aiming for. The project follows an n-tier architecture with elements of Domain-Driven Design, reflected in the structured separation of concerns and the inclusion of repository interfaces in the domain layer. This approach allowed my entities to encapsulate business logic where appropriate, for example the Game entity managing player interactions and deck operations, and helped enforce clear domain boundaries. Additionally, I ensured that the core functionalities were well-tested through unit and integration tests to improve maintainability and reliability.

One challenge I encountered was running all components (API, CLI, and PostgreSQL) within separate Docker containers simultaneously. While the Docker setup itself was functional, having two Spring Boot applications (one for the CLI and one for the API) led to internal issues. Indeed, the CLI was unable to communicate with the API, consistently returning 500 errors due to unexpected static resource handling. Despite multiple attempts to resolve this by ensuring proper class scanning and verifying the Maven-built JARs, Spring still failed to detect and wire components correctly. In hindsight, integrating a simple web interface that interacts with the API via Axios would have been a better solution, avoiding the complexities of managing multiple Spring Boot applications within Docker.

Additionally, there were a few features I intended to implement but did not have the time to complete. These include deploying the application online and adding more extensive validation across various API endpoints to ensure data integrity and improve user input handling. These enhancements would further improve the usability, reliability, and accessibility of the application in a real-world scenario.

---


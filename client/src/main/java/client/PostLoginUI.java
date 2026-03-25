package client;


import chess.ChessGame;
import chess.ChessBoard;
import model.GameData;
import model.AuthData;

import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade server;
    private final Scanner scanner;
    private final AuthData auth;

    public PostLoginUI(ServerFacade server,  Scanner scanner, AuthData auth) {
        this.server = server;
        this.scanner = scanner;
        this.auth = auth;
    }

    public boolean run() {
        System.out.println("Logged in as " + auth.username() + ". Type 'help' for commands.");
        while (true) {
            System.out.print("[" + auth.username() + "] >>> ");
            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");
            String cmd = parts[0].toLowerCase();

            switch (cmd) {
                case "help" -> printHelp();
                case "list" -> System.out.println("list wip");
                case "create" -> handleCreate();
                case "play" -> System.out.println("play wip");
                case "observe" -> System.out.println("observe wip");
                case "logout" -> { handleLogout(); return true; }
                case "quit" -> { return false;}
                default -> System.out.println("Unknown command. Type 'help' for commands.");

            }
        }
    }

    private void printHelp() {
        System.out.println("""
          create - create a new game
          list - list all games
          play - join a game as a player
          observe - observe a game
          logout - log out
          quit - exit the program
          help - show these commands
        """);
    }

    private void handleLogout() {
        try {
            server.logout(auth.authToken());
            System.out.println("Logged out");
        }catch (ClientException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void handleCreate() {
        System.out.print("Game name: ");
        String gameName = scanner.nextLine().trim();
        if (gameName.isBlank()) {
            System.out.println("Error: game name cant be blank.");
            return;
        }
        try {
            int gameID = server.createGame(gameName, auth.authToken());
            System.out.println("Game created with ID " + gameID);

        } catch (ClientException ex) {
            System.out.println(ex.getMessage());
        }
    }




}

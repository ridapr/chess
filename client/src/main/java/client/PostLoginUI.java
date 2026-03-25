package client;


import chess.ChessGame;
import chess.ChessBoard;
import model.GameData;
import model.AuthData;
import ui.DrawBoardUI;
import ui.EscapeSequences;

import java.util.Scanner;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class PostLoginUI {
    private final ServerFacade server;
    private final Scanner scanner;
    private final AuthData auth;

    private List<GameData> gameList = new ArrayList<>();

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
                case "list" -> handleList();
                case "create" -> handleCreate();
                case "play" -> handlePlay();
                case "observe" -> handleObserve();
                case "logout" -> { handleLogout(); return true; }
                case "quit" -> { return false;}
                default -> System.out.println("Unknown command. Type 'help' for commands.");

            }
        }
    }

    private void printHelp() {
        System.out.println(
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  help" +
            EscapeSequences.SET_TEXT_COLOR_WHITE + " - show these commands\n" +
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  create" +
            EscapeSequences.SET_TEXT_COLOR_WHITE + " - create a new game\n" +
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  list" +
            EscapeSequences.SET_TEXT_COLOR_WHITE + " - list all games\n" +
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  play" +
            EscapeSequences.SET_TEXT_COLOR_WHITE + " - join a game\n" +
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  observe" +
            EscapeSequences.SET_TEXT_COLOR_WHITE + " - observe a game\n" +
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  logout" +
            EscapeSequences.SET_TEXT_COLOR_WHITE + " - log out\n" +
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  quit" +
            EscapeSequences.SET_TEXT_COLOR_WHITE  + " - exit the program\n" +
            EscapeSequences.RESET_TEXT_COLOR
        );

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

    private void handleList() {
        try {
            Collection<GameData> games = server.listGames(auth.authToken());
            gameList = new ArrayList<>(games);
            if (gameList.isEmpty()) {
                System.out.println("No games exist.");
                return;
            }
            System.out.println("Games:");
            for (int i = 0; i< gameList.size(); i++) {
                GameData gd = gameList.get(i);
                String white = gd.whiteUsername() != null ? gd.whiteUsername() : "(open)";
                String black = gd.blackUsername() != null ? gd.blackUsername() : "(open)";
                System.out.printf(" %d. %s [White: %s | Black: %s]%n", i+1, gd.gameName(), white, black);
            }

        } catch (ClientException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void handlePlay() {
        if (gameList.isEmpty()) {
            System.out.println("Run 'list' first to see all games");
            return;
        }

        System.out.print("Game number: ");
        int num = readInt();
        if (num < 1 || num > gameList.size()) {
            System.out.println("Error: invalid game number.");
            return;
        }

        System.out.print("Color (WHITE/BLACK): ");
        String color = scanner.nextLine().trim().toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Error: color has to be WHITE or BLACK.");
            return;
        }

        GameData game = gameList.get(num - 1);
        try {
            server.joinGame(game.gameID(), color, auth.authToken());
            System.out.println("Joined game '" + game.gameName() + "' as " + color);
            drawBoard(color);
        } catch (ClientException ex) {
            System.out.println(ex.getMessage());
        }
    }


    private void handleObserve() {
        if (gameList.isEmpty()) {
            System.out.println("Run 'list' first to see available games.");
            return;
        }

        System.out.print("Game number: ");
        int num = readInt();
        if (num < 1 || num > gameList.size()) {
            System.out.println("Error: invalid game number.");
            return;
        }

        GameData game = gameList.get(num - 1);
        System.out.println("Observing game '" + game.gameName() + "'.");
        drawBoard("WHITE");

    }


    private void drawBoard(String color) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        DrawBoardUI.draw(board, color);
    }

    // refacator for code quality check
    private int readInt() {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}

package client;

import model.AuthData;
import java.util.Scanner;
import ui.EscapeSequences;

public class PreLoginUI {
    private final ServerFacade server;
    private final Scanner scanner;

    public PreLoginUI(ServerFacade server, Scanner scanner) {
        this.server = server;
        this.scanner = scanner;
    }

    // returns auth data when user logs in
    public AuthData run() {
        System.out.println("Welcome. Type help to view commands.");

        while (true) {
            System.out.print("[LOGGED OUT] >>> ");
            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");
            String entered = parts[0].toLowerCase();

            switch (entered) {
                case "help" -> printHelp();
                case "quit" -> { return null; }
                case "register" -> { AuthData auth = handleRegister();
                                   if (auth != null) {return auth; } }
                case "login" -> { AuthData auth = handleLogin();
                                 if (auth != null) { return auth; } }

                default -> System.out.println("Unknown Command. Type 'help' to see commands");

            }
        }
    }

    private void printHelp() {
        System.out.println(
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  register" +
            EscapeSequences.SET_TEXT_COLOR_WHITE + " - create a new account\n" +
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  login" +
            EscapeSequences.SET_TEXT_COLOR_WHITE + " - login to an existing account\n" +
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  quit" +
            EscapeSequences.SET_TEXT_COLOR_WHITE + " - exit the program\n" +
            EscapeSequences.SET_TEXT_COLOR_YELLOW + "  help" +
            EscapeSequences.SET_TEXT_COLOR_WHITE + " - show this help text\n" +
            EscapeSequences.RESET_TEXT_COLOR
        );
    }

    private AuthData handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (username.isBlank() || password.isBlank()) {
            System.out.println("Error: username and password are required.");
            return null;
        }

        try {
            AuthData auth = server.login(username, password);
            return auth;
        } catch (ClientException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private AuthData handleRegister() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            System.out.println("Error: all feilds are required.");
            return null;
        }

        try {
            AuthData auth = server.register(username, password, email);
            System.out.println("Registered and logged in as " + auth.username() + ".");
            return auth;
        } catch (ClientException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}

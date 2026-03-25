package client;

import model.AuthData;

import java.util.Scanner;

public class PreLoginUI {
    private final ServerFacade server;
    private final Scanner scanner;

    public PreLoginUI(ServerFacade server, Scanner scanner) {
        this.server = server;
        this.scanner = scanner;
    }

    // returns auth data when user logs in
    public AuthData run() {
        System.out.println("Welcome. type help");

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
                case "login" -> System.out.println("wip login");

                default -> System.out.println("Unknown Command. Type 'help' to see commands");

            }
        }
    }

    private void printHelp() {
        System.out.println("""
                help - shows these command options
                login - log in to an existing account
                register - create a new account
                quit - exit the program
                """);
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

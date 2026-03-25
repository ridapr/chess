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
                case "help" -> System.out.println("wip help");
                case "quit" -> { return null; }
                case "register" -> System.out.println("wip auth");

                default -> System.out.println("Unknown Command. Type 'help' to see commands");

            }
        }
    }
}

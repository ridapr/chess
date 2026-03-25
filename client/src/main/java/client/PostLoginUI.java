package client;

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
                case "help" -> System.out.println("help wip");
                case "list" -> System.out.println("list wip");
                case "create" -> System.out.println("create wip");
                case "play" -> System.out.println("play wip");
                case "observe" -> System.out.println("observe wip");
                case "logout" -> System.out.println("logout wip");
                case "quit" -> { return false;}
                default -> System.out.println("Unknown command. Type 'help' for commands.");

            }
        }
    }



}

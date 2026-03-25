package client;

import java.util.Scanner;

import model.AuthData;


public class Repl {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);

    public Repl(int port) {
        this.server =new ServerFacade(port);
    }

    public void run() {
        // pre login ui
        while (true) {
            AuthData auth = new PreLoginUI(server, scanner).run();
            if (auth == null) {
                System.out.println("Goodbye");
                return;
            }

            // post log in ui
        }

    }
}

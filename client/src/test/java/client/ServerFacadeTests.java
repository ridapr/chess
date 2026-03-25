package client;

import org.junit.jupiter.api.*;
import server.Server;

import model.GameData;
import model.AuthData;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    void clear() throws Exception {
        facade.clear();
    }
//    @Test
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }

    @Test
    void registerPositive() throws Exception {
        AuthData auth = facade.register("alice", "password", "alice@example.com");
        assertNotNull(auth.authToken());
        assertTrue(auth.authToken().length() > 10);
        assertEquals("alice", auth.username());

    }
    @Test
    void registerNegativeDupliate() throws Exception {
       facade.register("byu", "password", "byu@example.com");
       assertThrows(ClientException.class, () -> facade.register(
               "byu", "diffpassword", "byu2@example.com"));
    }


    @Test
    void loginPositive() throws Exception {
        facade.register("obama", "password", "obama@example.com");
        AuthData auth = facade.login("obama", "password");
        assertNotNull(auth.authToken());
        assertEquals("obama", auth.username());
    }
    @Test
    void loginNegativeWrongPassword() throws Exception {
        facade.register("foo", "password", "foo@e.com");
        assertThrows(ClientException.class, () -> facade.login("foo", "wrongpassword"));
    }


    @Test
    void logoutPositive() throws Exception {
        AuthData auth = facade.register("coug", "password", "coug@e.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }
    @Test
    void logoutNegativeInvalidToken() {
        assertThrows(ClientException.class, () -> facade.logout("notoken"));
    }





    @Test
    void createGamePositive() throws Exception {
        AuthData auth = facade.register("foo", "password", "foo@e.com");
        int gameID = facade.createGame("My Game", auth.authToken());
        assertTrue(gameID > 0);
    }
    @Test
    void createGameNegativeUnathorized() {
        assertThrows(ClientException.class, () -> facade.createGame("My game", "notoken"));
    }

    @Test
    void listGamesPositive() throws Exception {
         AuthData auth = facade.register("coug", "password", "coug@e.com");
        facade.createGame("game 1", auth.authToken());
        facade.createGame("game 2", auth.authToken());
        Collection<GameData> games = facade.listGames(auth.authToken());
        assertEquals(2, games.size());
    }
    @Test
    void listGamesNegUnauthorized() {
        assertThrows(ClientException.class, () -> facade.listGames("notoken"));
    }


    @Test
    void joinGamePositive() throws Exception {
        AuthData auth = facade.register("coug", "password", "coug@e.com");
        int gameID = facade.createGame("game 1", auth.authToken());
        assertDoesNotThrow(() -> facade.joinGame(gameID, "WHITE", auth.authToken()));
    }
    @Test
    void joinGameNegativeColorTaken() throws Exception {
        AuthData auth1 = facade.register("coug", "password1", "coug@e.com");
        AuthData auth2 = facade.register("cosmo", "password2", "cosmo@e.com");
        int gameID = facade.createGame("game1", auth1.authToken());
        facade.joinGame(gameID, "WHITE", auth1.authToken());
        assertThrows(ClientException.class, () -> facade.joinGame(gameID, "WHITE", auth2.authToken()));
    }


}

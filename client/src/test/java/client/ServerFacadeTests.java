package client;

import org.junit.jupiter.api.*;
import server.Server;

import model.AuthData;

import static org.junit.jupiter.api.Assertions.*;


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






}

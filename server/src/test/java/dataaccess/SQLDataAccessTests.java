package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.AuthData;
import model.UserData;

import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

public class SQLDataAccessTests {
    private static MySQLDataAccess tests;

    @BeforeAll
    static void setup() throws DataAccessException {
        tests = new MySQLDataAccess();

    }

    @BeforeEach
    void clearDatabase() throws DataAccessException {
        tests.clear();
    }

    @Test
    void clearPositive() throws DataAccessException {
        tests.createUser(new UserData("foo", "password", "foo@example.com"));
        tests.clear();
        assertNull(tests.getUser("foo"), "User should be gone after clear");
    }


    @Test
    void createUserPositive() throws DataAccessException {
        tests.createUser(new UserData("foo", "password", "foo@example.com"));
        UserData stored = tests.getUser("foo");

        assertNotNull(stored);
        assertEquals("foo", stored.username());
        assertEquals("foo@example.com", stored.email());
        assertTrue(BCrypt.checkpw("password", stored.password()),
                "stored password should be hash of 'password'");
    }

    @Test
    void createUserNegative() throws DataAccessException {
        tests.createUser(new UserData("foo", "password", "foo@example.com"));
        assertThrows(DataAccessException.class,
                () -> tests.createUser(new UserData("foo", "other", "foo2@example.com")),
                "throw when inserting a duplicate username");
    }


    @Test
    void getUserPositive() throws DataAccessException {
        tests.createUser(new UserData("foo", "password", "foo@example.com"));
        UserData user = tests.getUser("foo");
        assertNotNull(user);
        assertEquals("foo", user.username());
    }

    @Test
    void getUserNegative() throws DataAccessException {
        assertNull(tests.getUser("nobody"), "Should return null, does not exist");
    }



    @Test
    void createAuthPositive() throws DataAccessException {
        tests.createAuth(new AuthData("token-abc", "foo"));
        AuthData auth = tests.getAuth("token-abc");
        assertNotNull(auth);
        assertEquals("foo", auth.username());
    }

    @Test
    void createAuthNegative() throws DataAccessException {
        tests.createAuth(new AuthData("same-token", "foo"));
        assertThrows(DataAccessException.class, () -> tests.createAuth(new AuthData(
                "same-token", "bar")), "Should throw on duplicate auth token");
    }



    @Test
    void getAuthPositive() throws DataAccessException {
        tests.createAuth(new AuthData("my-token", "foo"));
        AuthData auth = tests.getAuth("my-token");
        assertNotNull(auth);
        assertEquals("foo", auth.username());
        assertEquals("my-token", auth.authToken());
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        assertNull(tests.getAuth("nonexistent-token"), "Should return null, token does not exist");
    }



    @Test
    void deleteAuthPositive() throws DataAccessException {
        tests.createAuth(new AuthData("delete-token", "foo"));
        tests.deleteAuth("delete-token");
        assertNull(tests.getAuth("delete-token"), "Auth token should be null after deletion");
    }

    void deleteAuthNegative() {
        assertDoesNotThrow(() -> tests.deleteAuth("ghost-token"),
                "Deleting no token should not throw");
    }



    @Test
    void createGamePositive() throws DataAccessException {
        GameData created = tests.createGame(
                new GameData(0, null, null, "My Game", new ChessGame()));
        assertTrue(created.gameID() > 0, "gameID should be positive num");
        assertEquals("My Game", created.gameName());
    }

    @Test
    void createGameNegative() {
        assertThrows(DataAccessException.class, () -> tests.createGame(
                        new GameData(0, null, null, null, new ChessGame())),
                "Should throw when gameName is null");
    }


    @Test
    void getGamePositive() throws DataAccessException {
        GameData created = tests.createGame(
                new GameData(0, null, null, "Test Game", new ChessGame()));
        GameData fetched = tests.getGame(created.gameID());
        assertNotNull(fetched);
        assertEquals("Test Game", fetched.gameName());
        assertEquals(created.gameID(), fetched.gameID());
    }

    @Test
    void getGameNegative() throws DataAccessException {
        assertNull(tests.getGame(9999), "Should return null for ID that does not exist");
    }


    @Test
    void updateGamePositive() throws DataAccessException {
        GameData created = tests.createGame(
                new GameData(0, null, null, "Test", new ChessGame()));

        GameData updated = new GameData(
                created.gameID(), "whitePlayer", null, "Test", created.game());
        tests.updateGame(updated);

        GameData fetched = tests.getGame(created.gameID());
        assertNotNull(fetched);
         assertEquals("whitePlayer", fetched.whiteUsername());
    }

    @Test
    void updateGameNegative() {
        // doesnt throw just does nothing
        GameData phantom = new GameData(99999, "ghost",
                null, "Phantom", new ChessGame());
        assertDoesNotThrow(() -> tests.updateGame(phantom), "Updating no game should not throw an ex");
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        tests.createGame(new GameData(0, null, null, "Game1", new ChessGame()));
        tests.createGame(new GameData(0, null, null, "Game2", new ChessGame()));
        tests.createGame(new GameData(0, null, null, "Game3", new ChessGame()));

        Collection<GameData> games = tests.listGames();
        assertEquals(3, games.size(), "should list all 3 games");
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        Collection<GameData> games = tests.listGames();
        assertNotNull(games);
        assertTrue(games.isEmpty(), "should return an empty list because no games");
    }

}


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

}


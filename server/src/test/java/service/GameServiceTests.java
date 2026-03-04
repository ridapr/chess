package service;

import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private UserService userService;
    private GameService gameService;
    private String validToken;

    @BeforeEach
    void setUp() throws ServiceException {
        var db = new MemoryDataAccess();
        userService = new UserService(db);
        gameService = new GameService(db, userService);

        var result = userService.register(new UserService.RegisterRequest("foo", "password", "foo@example.com"));

        validToken = result.authToken();
    }



    @Test
    void createGameSuccess() throws ServiceException {
        var result = gameService.createGame(validToken, new GameService.CreateGameRequest("Game"));

        assertTrue(result.gameID () >0);
    }
    @Test
    void createGameInvalidToken() {
        ServiceException ex = assertThrows(ServiceException.class, () -> gameService.createGame("badToken", new GameService.CreateGameRequest("Game")));

        assertEquals(401, ex.statusCode());
    }

    @Test
    void createGameNoName() {
        ServiceException ex = assertThrows(ServiceException.class, () -> gameService.createGame(validToken, new GameService.CreateGameRequest("")));

        assertEquals(400, ex.statusCode());
    }




    @Test
    void listGamesSuccess() throws ServiceException {
        gameService.createGame(validToken, new GameService.CreateGameRequest("Game1"));
        gameService.createGame(validToken, new GameService.CreateGameRequest("Game2"));

        var games = gameService.listGames(validToken);

        assertEquals(2, games.size());
    }
    @Test
    void listGamesUnauthorizedThrows() {
        ServiceException ex = assertThrows(ServiceException.class, () -> gameService.listGames("invalidToken"));

        assertEquals(401, ex.statusCode());
    }


    @Test
    void joinGameSuccess() throws ServiceException {
        var created = gameService.createGame(validToken, new GameService.CreateGameRequest(("Game")));

        assertDoesNotThrow(() -> gameService.joinGame(validToken, new GameService.JoinGameRequest("WHITE", created.gameID())));
    }
    @Test
    void joinGameColorTaken() throws ServiceException {
        var created = gameService.createGame(validToken, new GameService.CreateGameRequest("Game"));
        gameService.joinGame(validToken, new GameService.JoinGameRequest("WHITE", created.gameID()));

        var player2 = userService.register(new UserService.RegisterRequest("player2", "password", "p2@example.com"));

        ServiceException ex = assertThrows(ServiceException.class, () -> gameService.joinGame(player2.authToken(), new GameService.JoinGameRequest("WHITE", created.gameID())));

        assertEquals(403, ex.statusCode());
    }
    @Test
    void joinGameWrongGameID() {
        ServiceException ex = assertThrows(ServiceException.class, () -> gameService.joinGame(validToken, new GameService.JoinGameRequest("BLACK", 9999)));

        assertEquals(400, ex.statusCode());
    }
}

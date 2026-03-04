package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final DataAccess db;
    private final UserService userService;

    public GameService(DataAccess db, UserService userService) {
        this.db = db;
        this.userService = userService;
    }

    public record CreateGameRequest(String gameName) {}
    public record CreateGameResult(int gameID) {}
    public record JoinGameRequest(String playerColor, int gameID) {}

    public Collection<GameData> listGames(String authToken) throws ServiceException {
        userService.validateToken(authToken);
        try {
            return db.listGames();
        } catch (DataAccessException exception) {
            throw new ServiceException(500, "Error: " + exception.getMessage());
        }
    }


    public CreateGameResult createGame(String authToken, CreateGameRequest req) throws ServiceException {
        userService.validateToken(authToken);
        if (req.gameName() == null || req.gameName().isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }

        try {
            GameData created = db.createGame(new GameData(0, null, null, req.gameName(), new ChessGame()));
            return new CreateGameResult(created.gameID());
        } catch (DataAccessException exception) {
            throw new ServiceException(500, "Error: " + exception.getMessage());
        }

    }


}

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

    public Collection<GameData> listGames(String authToken) throws SericeException {

    }
}

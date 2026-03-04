package service;

import chess.ChessGame;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

import model.GameData;
import model.AuthData;

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


    public void joinGame(String authToken, JoinGameRequest req) throws ServiceException {
        AuthData auth = userService.validateToken(authToken);

        if (req.playerColor() == null || req.gameID() <= 0) {
            throw new ServiceException(400, "Error: bad request");
        }

        String color = req.playerColor().toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new ServiceException(400, "Error: bad request. Not White or Black");
        }

        GameData game;
        try {
            game = db.getGame(req.gameID());
        } catch (DataAccessException exception) {
            throw new ServiceException(500, "Error: " + exception.getMessage());
        }

        if (game == null) {
            throw new ServiceException(400, "Error: bad request");
        }

        String newWhite = game.whiteUsername();
        String newBlack = game.blackUsername();

        // check if already taken
        if (color.equals("WHITE")) {
            if (newWhite != null) {
                throw new ServiceException(403, "Error: already taken");

            }
            newWhite = auth.username();
        } else {
            if (newBlack != null) {
                throw new ServiceException(403, "ERror: already taken");
            }
            newBlack = auth.username();
        }

        try {
            db.updateGame(new GameData(game.gameID(), newWhite, newBlack, game.gameName(), game.game()));
        } catch (DataAccessException exception) {
            throw new ServiceException(500, "Error: " + exception.getMessage());
        }
    }


}

package dataaccess;

import model.AuthData;
import model.UserData;
import model.GameData;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class MemoryDataAccess implements DataAccess {
    private Map<String, UserData> users = new HashMap<>();
    private Map<Integer, GameData> games = new HashMap<>();
    private Map<String, AuthData> auths = new HashMap<>();
    int nextGameId = 1;

    @Override
    public void clear() {
        users.clear();
        games.clear();
        auths.clear();
        nextGameId = 1;
    }



    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already taken: " + user.username());
        }
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        users.put(user.username(), new UserData(user.username(), hashedPassword, user.email()));
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }



    @Override
    public GameData createGame(GameData game) {
        GameData saved = new GameData(nextGameId++, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(saved.gameID(), saved);
        return saved;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found: " + game.gameID());
        }
        games.put(game.gameID(), game);
    }



    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found: " + authToken);
        }
        auths.remove(authToken);
    }

}

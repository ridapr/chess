package dataaccess;

import model.AuthData;
import model.UserData;
import model.GameData;

import java.util.HashMap;
import java.util.Map;

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
}

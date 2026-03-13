package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import model.AuthData;

import org.mindrot.jbcrypt.BCrypt;
import com.google.gson.Gson;

import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.util.Collection;



public class MySQLDataAccess implements DataAccess {
    private final Gson gson = new Gson();

    public MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              username varchar(256) NOT NULL,
              password varchar(256) NOT NULL,
              email varchar(256) NOT NULL,
              PRIMARY KEY (username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
              authToken varchar(256) NOT NULL,
              username varchar(256) NOT NULL,
              PRIMARY KEY (authToken)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
              gameID INT NOT NULL AUTO_INCREMENT,
              whiteUsername varchar(256) DEFAULT NULL,
              blackUsername varchar(256) DEFAULT NULL,
              gameName varchar(256) NOT NULL,
              game TEXT NOT NULL,
              PRIMARY KEY (gameID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        for (String table: new String[]{"auth" ,"games", "users"}) {
            executeUpdate("TRUNCATE TABLE " + table);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement, user.username(), hashedPassword, user.email());
        } catch (DataAccessException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Error: username already taken");
            }
            throw ex;
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM users WHERE username=?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"), rs.getString("password"), rs.getString("email")
                    );
                }
            }
        }catch (SQLException ex) {
            throw new DataAccessException("UNable to read user: " + ex.getMessage(), ex);
        }
        return null;
    }



    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        }catch (SQLException exception) {
            throw new DataAccessException("Unable to read auth: " + exception.getMessage(), exception);
        }
        return null;

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        executeUpdate("DELETE FROM auth WHERE authToken=?", authToken);
    }



    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String json = gson.toJson(game.game());
        int id = executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), json);
        return new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    String json = rs.getString("game");

                    ChessGame chessGame = gson.fromJson(json, ChessGame.class);
                    return new GameData(id, whiteUsername, blackUsername, gameName, chessGame);

                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to read game: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }


    // helper based on pet shop
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement
                (statement, RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String p) {
                    ps.setString(i + 1, p);
                } else if (param instanceof Integer p) {
                    ps.setInt(i + 1, p);
                } else if (param == null) {
                    ps.setNull(i + 1, NULL);
                }
             }

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database: " + e.getMessage(), e);
        }

    }
}

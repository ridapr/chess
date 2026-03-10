package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import model.AuthData;

import org.mindrot.jbcrypt.BCrypt;
import com.google.gson.Gson;

import java.sql.*;



public class MySQLDataAccess implements DataAccess {
    private final Gson gson = new Gson();

    public MYSQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    // copied directly from petshop for now as placeholder
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  pet (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
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
}

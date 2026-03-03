package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;


public class UserService {
    private final DataAccess db;

    public UserService(DataAccess db) {
        this.db = db;
    }

    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken) {}


    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest req) throws ServiceException {
        if (req.username() == null || req.username().isBlank() ||
            req.password() == null || req.password().isBlank() ||
            req.email() == null || req.email().isBlank()) {
                throw new ServiceException(400, "Error: bad request");
        }

        try {
            db.createUser(new UserData(req.username(), req.password(), req.email()));
        } catch (DataAccessException exception) {
            throw new ServiceException(403, "Error: username already taken");
        }

        String token = generateToken();
        try {
            db.createAuth(new AuthData(token, req.username()));
        } catch (DataAccessException exception) {
            throw new ServiceException(500, "Error: " + exception.getMessage());
        }

        return new RegisterResult(req.username(), token);
    }
}

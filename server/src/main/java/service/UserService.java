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
    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken) {}


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


    public LoginResult login(LoginRequest req) throws ServiceException {
        if (req.username() == null || req.username().isBlank() ||
            req.password() == null || req.password().isBlank()) {
                throw new ServiceException(400, "Error: bad request");
        }

        UserData user;
        try {
            user = db.getUser(req.username());
        } catch (DataAccessException exception) {
            throw new ServiceException(500, "Error: " + exception.getMessage());
        }

        if (user == null || !user.password().equals(req.password())) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        String token = generateToken();
        try {
            db.createAuth(new AuthData(token, req.username()));
        } catch (DataAccessException exception) {
            throw new ServiceException(500, "Error: " + exception.getMessage());
        }

        return new LoginResult(req.username(), token);
    }


    public void logout(String authToken) throws ServiceException {
//        if (authToken == null || authToken.isBlank()) {
//            throw new ServiceException(401, "Error: unauthorized");
//        }
//        try {
//            AuthData auth = db.getAuth(authToken);
//            if (auth == null) {
//                throw new ServiceException(401, "Error: unauthorized");
//            }
//            db.deleteAuth(authToken);
//
//        } catch (DataAccessException exception) {
//            throw new ServiceException(500, "Error: " + exception.getMessage());
//        }
        validateToken(authToken);
        try {
            db.deleteAuth(authToken);
        } catch (DataAccessException exception) {
            throw new ServiceException(500, "Error: " + exception.getMessage());
        }
    }



    protected AuthData validateToken(String authToken) throws ServiceException {
        if (authToken == null || authToken.isBlank()) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        try {
            AuthData auth = db.getAuth(authToken);
            if (auth == null) {
                throw new ServiceException(401, "Error: unauthorized");
            }
            return auth;
        } catch (DataAccessException exception) {
            throw new ServiceException(500, "Error: " + exception.getMessage());
        }
    }


}

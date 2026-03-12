package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySQLDataAccess;
import service.ClearService;
import service.UserService;
import service.GameService;
import service.ServiceException;
import model.GameData;

import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import java.util.Map;
import java.util.Collection;

public class Server {

    private final Javalin javalin;

     // will need one for each servcie
//    private final DataAccess db = new MemoryDataAccess();
//
//    private final ClearService clearService = new ClearService(db);
//    private final UserService userService = new UserService(db);
//    private final GameService gameService = new GameService(db, userService);

    private final DataAccess db;
    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        DataAccess dataAccess;
        try {
            dataAccess = new MySQLDataAccess();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to initialize db: " + ex.getMessage(), ex);
        }
        db = dataAccess;
        clearService = new ClearService(db);
        userService = new UserService(db);
        gameService = new GameService(db, userService);





        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalin.delete("/db", this::handleClear);
        javalin.post("/user", this::handleRegister);
        javalin.post("/session", this::handleLogin);
        javalin.delete("/session", this::handleLogout);
        javalin.get("/game", this::handleListGames);
        javalin.post("/game", this::handleCreateGame);
        javalin.put("/game", this::handleJoinGame);



         javalin.exception(ServiceException.class, this::exceptionHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(ServiceException exception, Context context) {
        context.status(exception.statusCode());
        context.result(new Gson().toJson(Map.of("message", exception.getMessage())));
    }

    private void handleClear(Context context) throws ServiceException {
        clearService.clear();
        context.result("{}");
    }

    private void handleRegister(Context context) throws ServiceException {
        var req = new Gson().fromJson(context.body(), UserService.RegisterRequest.class);
        var result = userService.register(req);
        context.result(new Gson().toJson(result));
    }

    private void handleLogin(Context context) throws ServiceException {
        var req = new Gson().fromJson(context.body(), UserService.LoginRequest.class);
        var result = userService.login(req);
        context.result(new Gson().toJson(result));
    }

    private void handleLogout(Context context) throws ServiceException {
        String token = context.header("authorization");
        userService.logout(token);
        context.result("{}");
    }

    private void handleListGames(Context context) throws ServiceException {
        String token = context.header("authorization");
        Collection<GameData> games = gameService.listGames(token);
        context.result(new Gson().toJson(Map.of("games", games)));
    }

    private void handleCreateGame(Context context) throws ServiceException {
        String token = context.header("authorization");
        var req = new Gson().fromJson(context.body(), GameService.CreateGameRequest.class);
        var result = gameService.createGame(token, req);
        context.result(new Gson().toJson(result));
    }

    private void handleJoinGame(Context context) throws ServiceException {
        String token = context.header("authorization");
        var req = new Gson().fromJson(context.body(), GameService.JoinGameRequest.class);
        gameService.joinGame(token, req);
        context.result("{}");
    }


}

package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import service.ClearService;
import service.UserService;
import service.ServiceException;

import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import java.util.Map;

public class Server {

    private final Javalin javalin;

     // will need one for each servcie
    private final DataAccess db = new MemoryDataAccess();

    private final ClearService clearService = new ClearService(db);
    private final UserService userService = new UserService(db);

    public Server() {
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

    }

    private void handleListGames(Context context) throws ServiceException {

    }

    private void handleCreateGame(Context context) throws ServiceException {

    }

    private void handleJoinGame(Context context) throws ServiceException {

    }


}

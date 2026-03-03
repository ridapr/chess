package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import service.ClearService;
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

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalin.delete("/db", this::handleClear);

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
}

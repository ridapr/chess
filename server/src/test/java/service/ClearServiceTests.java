package service;

import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
     @Test
    void clearSucceeds() throws ServiceException {
        var db = new MemoryDataAccess();
        var userService = new UserService(db);
        var gameService = new GameService(db, userService);
        var clearService = new ClearService(db);

        // starting data
         var reg = userService.register(new UserService.RegisterRequest("foo", "password", "foo@example.com"));
         gameService.createGame(reg.authToken(), new GameService.CreateGameRequest("testGame"));

         assertDoesNotThrow(clearService::clear);

         ServiceException ex = assertThrows(ServiceException.class, () -> userService.login(new UserService.LoginRequest("foo", "password")));
         assertEquals(401, ex.statusCode());
     }
}

package service;

import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new MemoryDataAccess());
    }

    @Test
    void registerSuccess() throws ServiceException {
        var result = userService.register(new UserService.RegisterRequest("foo", "password", "foo@example.com"));

        assertEquals("foo", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isBlank());
    }
    @Test
    void registerDuplicate() throws ServiceException {
        userService.register(new UserService.RegisterRequest("foo", "password", "foo@example.com"));

        ServiceException exception = assertThrows(ServiceException.class, () -> userService.register(new UserService.RegisterRequest("foo", "otherpassword", "foo2@example.com")));

        assertEquals(403, exception.statusCode());
    }
     @Test
    void registerEmptyField() throws ServiceException {
        ServiceException ex = assertThrows(ServiceException.class, () -> userService.register(new UserService.RegisterRequest("", "password", "foo@example.com")));
        assertEquals(400, ex.statusCode());
    }




     @Test
    void loginSuccess() throws ServiceException {
        userService.register(new UserService.RegisterRequest("foo", "password", "foo@example.com"));

        var result = userService.login(new UserService.LoginRequest("foo", "password"));

        assertEquals("foo", result.username());
        assertNotNull(result.authToken());
    }
    @Test
    void loginWrongPassword() throws ServiceException {
        userService.register(new UserService.RegisterRequest("foo", "password", "foo@example.com"));

        ServiceException exception = assertThrows(ServiceException.class, () -> userService.login(new UserService.LoginRequest("foo", "wrongpassword")));

        assertEquals(401, exception.statusCode());
    }
    @Test
    void loginUnknownUser() {
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.login(new UserService.LoginRequest("foo", "password")));

        assertEquals(401, exception.statusCode());
    }



    @Test
    void logoutSuccess() throws ServiceException {
        var reg = userService.register(new UserService.RegisterRequest("foo", "password", "foo@example.com"));

        assertDoesNotThrow(() -> userService.logout(reg.authToken()));
    }
    @Test
    void logoutInvalidToken() {
        ServiceException ex = assertThrows(ServiceException.class, () -> userService.logout("fakeToken"));

        assertEquals(401, ex.statusCode());
    }

}

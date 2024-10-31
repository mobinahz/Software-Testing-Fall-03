package mizdooni.controllers;


import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import mizdooni.exceptions.DuplicatedUsernameEmail;
import mizdooni.exceptions.InvalidEmailFormat;
import mizdooni.exceptions.InvalidUsernameFormat;
import mizdooni.model.Address;
import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ServiceUtils;
import mizdooni.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class AuthenticationControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private ServiceUtils serviceUtils;
    User mockUser;
    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User("test_user", "test_pass", "test@gmail.com", null, User.Role.client);
    }

    @Test
    public void testUserWhenIsLoggedIn() {
        when(userService.getCurrentUser()).thenReturn(mockUser);

        Response response = authenticationController.user();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("current user", response.getMessage());
        assertEquals(mockUser, response.getData());
    }

    @Test
    public void testUserWhenNotLoggedIn() {
        when(userService.getCurrentUser()).thenReturn(null);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.user());

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    public void testLoginWhenParamsBlank() {
        when(userService.login("test_user", "test_pass")).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () ->
                authenticationController.login(Map.of("username", "", "password", "")));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    public void testLoginSuccess() {
        when(userService.login("test_user", "test_pass")).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(mockUser);

        Response response = authenticationController.login(Map.of("username", "test_user", "password", "test_pass"));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("login successful", response.getMessage());
        assertEquals(mockUser, response.getData());
    }

    @Test
    public void testLoginWhenWrongParams() {
        when(userService.login("test_user", "test_pass")).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () ->
                authenticationController.login(Map.of("username", "wrong_user", "password", "test_pass")));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("invalid username or password", exception.getMessage());
    }

    @Test
    public void testSignupWhenParamsMissing() {
        Map<String, Object> incompleteParams = Map.of(
                "username", "test_user",
                "password", "test_pass"
        );

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(incompleteParams));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    public void testSignupWhenParamsBadType() {
        Map<String, Object> badTypeParams = Map.of(
                "username", 1111, // Invalid type
                "password", "test_pass",
                "email", "test@gmail.com",
                "role", "client",
                "address", Map.of("country", "test_country", "city", "vice_city")
        );

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(badTypeParams));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    public void testSignupWhenParamRoleBadType() {
        Map<String, Object> badTypeParams = Map.of(
                "username", "test_user",
                "password", "test_pass",
                "email", "test@gmail.com",
                "role", "ADMIN_WhoShouldNotBeHere",
                "address", Map.of("country", "test_country", "city", "vice_city")
        );

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(badTypeParams));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    public void testSignupWhenParamsBlank() {
        Map<String, Object> params = Map.of(
                "username", "test_user",
                "password", "",
                "email", "aaa@gm.com",
                "role", "client",
                "address", Map.of("country", "test_country", "city", "vice_city")
        );

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    public void testSignupSuccess() {
        Map<String, Object> validParams = Map.of(
                "username", "test_user",
                "password", "test_pass",
                "email", "test@gmail.com",
                "role", "client",
                "address", Map.of("country", "test_country", "city", "vice_city")
        );

        Address address = new Address("test_country", "vice_city", null);
        try {
            doNothing().when(userService).signup("test_user", "test_pass", "test@gmail.com", address, User.Role.client);
        } catch (InvalidEmailFormat | InvalidUsernameFormat | DuplicatedUsernameEmail e) {
//            throw new RuntimeException(e);
        }

        when(userService.login("test_user", "test_pass")).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(mockUser);

        Response response = authenticationController.signup(validParams);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("signup successful", response.getMessage());
        assertEquals(mockUser, response.getData());
    }

    @Test
    public void testSignupWhenLoginFails() {
        Map<String, Object> validParams = Map.of(
                "username", "test_user",
                "password", "test_pass",
                "email", "test@gmail.com",
                "role", "client",
                "address", Map.of("country", "test_country", "city", "vice_city")
        );

        Address address = new Address("test_country", "vice_city", null);
        try {
            doNothing().when(userService).signup("test_user", "test_pass", "test@gmail.com", address, User.Role.client);
        } catch (InvalidEmailFormat | InvalidUsernameFormat | DuplicatedUsernameEmail e) {
            throw new RuntimeException(e);
        }

        doThrow(new RuntimeException("after signup, login failed")).when(userService).login("test_user", "test_pass");
        when(userService.getCurrentUser()).thenReturn(mockUser);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(validParams));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("after signup, login failed", exception.getMessage());
    }

    @Test
    public void testLogoutWhenLoggedOut() {
        when(userService.logout()).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.logout());

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    public void testLogoutWhenLoggedIn() {
        when(userService.logout()).thenReturn(true);

        Response response = authenticationController.logout();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("logout successful", response.getMessage());
    }

    @Test
    public void testValidateUsernameWhenInvalidFormat() {
        try (MockedStatic<ServiceUtils> utils = mockStatic(ServiceUtils.class)) {
            utils.when(() -> ServiceUtils.validateUsername("test_user")).thenReturn(false);

            ResponseException exception = assertThrows(ResponseException.class, () ->
                    authenticationController.validateUsername("test_user"));

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
            assertEquals("invalid username format", exception.getMessage());
        }
    }

    @Test
    public void testValidateUsernameWhenConflict() {
        when(userService.usernameExists("test_user")).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () ->
                authenticationController.validateUsername("test_user"));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("username already exists", exception.getMessage());
    }

    @Test
    public void testValidateUsernameWhenAvailable() {
        try (MockedStatic<ServiceUtils> utils = mockStatic(ServiceUtils.class)) {
            utils.when(() -> ServiceUtils.validateUsername("test_user")).thenReturn(true);
            when(userService.usernameExists("test_user")).thenReturn(false);

            Response response = authenticationController.validateUsername("test_user");

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals("username is available", response.getMessage());
        }
    }


    @Test
    public void testValidateEmailWhenInvalidFormat() {
        try (MockedStatic<ServiceUtils> utils = mockStatic(ServiceUtils.class)) {
            utils.when(() -> ServiceUtils.validateEmail("test@@gmail.com")).thenReturn(false);

            ResponseException exception = assertThrows(ResponseException.class, () ->
                    authenticationController.validateEmail("test@@gmail.com"));

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
            assertEquals("invalid email format", exception.getMessage());
        }
    }

    @Test
    public void testValidateEmailWhenConflict() {
        when(userService.emailExists("test@gmail.com")).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () ->
                authenticationController.validateEmail("test@gmail.com"));

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("email already registered", exception.getMessage());
    }

    @Test
    public void testValidateEmailWhenAvailable() {
        try (MockedStatic<ServiceUtils> utils = mockStatic(ServiceUtils.class)) {
            utils.when(() -> ServiceUtils.validateEmail("test@gmail.com")).thenReturn(true);
            when(userService.emailExists("test@gmail.com")).thenReturn(false);

            Response response = authenticationController.validateEmail("test@gmail.com");

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals("email not registered", response.getMessage());
        }
    }
}

package mizdooni.controllers;

import mizdooni.exceptions.*;
import mizdooni.model.*;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ReservationService;
import mizdooni.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationControllerTest {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private ReservationService reserveService;

    @InjectMocks
    private ReservationController reservationController;
    private Restaurant mockRestaurant;
    private Reservation mockReservation1;
    private Reservation mockReservation2;
    List<Reservation> mockReservations;
    List<LocalTime> mockTimes;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Address address = new Address("Iran", "Tehran", "Moj");
        User manager = new User("mobina", "12345", "mobinahz@gmail.com", address, User.Role.manager);
        mockRestaurant  = new Restaurant(
                "Symposium",
                manager,
                "England",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                "Be happy =)",
                address,
                "Beautiful.jpg"
        );
        User user = new User("mobina", "12345", "mobinahz@gmail.com", null, User.Role.client);
        User user2 = new User("mamad", "23456", "mamad@gmail.com", null, User.Role.client);
        Table table = new Table(1, 1, 4);
        mockReservation1 = new Reservation(user, mockRestaurant, table, LocalDateTime.of(2025, 10, 18, 19, 0));
        mockReservation2 = new Reservation(user2, mockRestaurant, table, LocalDateTime.of(2024, 10, 18, 19, 0));

        mockReservations = List.of(mockReservation1, mockReservation2);
        mockTimes = List.of(LocalTime.of(12, 0), LocalTime.of(22, 0));
    }

    @Test
    public void testGetReservationsWhenInvalidDate() {
        String invalidDateStr = "Not-A-Date";

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        ResponseException exception = assertThrows(ResponseException.class, () ->
                reservationController.getReservations(1, 5, invalidDateStr));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    public void testGetReservationsSuccess() {
        String dateStr = "2024-10-18";
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        try {
            when(reserveService.getReservations(1, 1, date)).thenReturn(mockReservations);
        } catch (RestaurantNotFound | UserNotManager | InvalidManagerRestaurant | TableNotFound e) {
//            throw new RuntimeException(e);
        }

        Response response = reservationController.getReservations(1, 1, dateStr);

        assertNotNull(response);
        assertEquals("restaurant table reservations", response.getMessage());
        assertEquals(mockReservations, response.getData());
    }

    @Test
    public void testGetReservationsFails() {
        String dateStr = "2024-10-18";
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        try {
            doThrow(new RuntimeException("failed to get reservations")).when(reserveService).getReservations(1, 1, date);
        } catch (RestaurantNotFound | UserNotManager | InvalidManagerRestaurant | TableNotFound e) {
//            throw new RuntimeException(e);
        }

        ResponseException exception = assertThrows(ResponseException.class, () ->
                reservationController.getReservations(1, 1, dateStr));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("failed to get reservations", exception.getMessage());
    }

    @Test
    public void testGetCustomerReservationsSuccess() {
        try {
            when(reserveService.getCustomerReservations(100)).thenReturn(mockReservations);
        } catch (UserNotFound | UserNoAccess e) {
//            throw new RuntimeException(e);
        }

        Response response = reservationController.getCustomerReservations(100);

        assertNotNull(response);
        assertEquals("user reservations", response.getMessage());
        assertEquals(mockReservations, response.getData());
    }

    @Test
    public void testGetCustomerReservationsFails() {
        try {
            doThrow(new RuntimeException("failed to get customer reservation")).when(reserveService).getCustomerReservations(100);
        } catch (UserNotFound | UserNoAccess e) {
//            throw new RuntimeException(e);
        }

        ResponseException exception = assertThrows(ResponseException.class, () ->
                reservationController.getCustomerReservations(100));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("failed to get customer reservation", exception.getMessage());
    }

    @Test
    public void testGetAvailableTimesWhenInvalidDate() {
        String invalidDateStr = "Not-A-Date";
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        ResponseException exception = assertThrows(ResponseException.class, () ->
                reservationController.getAvailableTimes(1, 1, invalidDateStr));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    public void testGetAvailableTimesSuccess() {
        String dateStr = "2024-10-18";
        LocalDate localDate = LocalDate.parse(dateStr);

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        try {
            when(reserveService.getAvailableTimes(1, 1, localDate)).thenReturn(mockTimes);
        } catch (RestaurantNotFound | DateTimeInThePast | BadPeopleNumber e) {
//            throw new RuntimeException(e);
        }

        Response response = reservationController.getAvailableTimes(1, 1, dateStr);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("available times", response.getMessage());
        assertEquals(mockTimes, response.getData());
    }

    @Test
    public void testGetAvailableTimesFails() {
        String dateStr = "2024-10-18";
        LocalDate localDate = LocalDate.parse(dateStr);

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        try {
            doThrow(new RuntimeException("failed to get available times")).when(reserveService).getAvailableTimes(1, 1, localDate);
        } catch (RestaurantNotFound | DateTimeInThePast | BadPeopleNumber e) {
//            throw new RuntimeException(e);
        }

        ResponseException exception = assertThrows(ResponseException.class, () ->
                reservationController.getAvailableTimes(1, 1, dateStr));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("failed to get available times", exception.getMessage());
    }

    @Test
    public void testAddReservationWhenParamsMissing() {
        Map<String, String> badParams = Map.of(
                "people", "1"
        );

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        ResponseException exception = assertThrows(ResponseException.class, () ->
                reservationController.addReservation(1, badParams));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    public void testAddReservationWhenBadType() {
        Map<String, String> badParams = Map.of(
                "people", "1",
                "datetime", "What is this?"
        );

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        ResponseException exception = assertThrows(ResponseException.class, () ->
                reservationController.addReservation(1, badParams));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    public void testAddReservationSuccess() {
        String dateStr = "2025-10-18 19:00";
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DATETIME_FORMATTER);
        Map<String, String> params = Map.of(
                "people", "1",
                "datetime", dateStr
        );

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        try {
            when(reserveService.reserveTable(1, 1, localDateTime))
                    .thenReturn(mockReservation1);
        } catch (UserNotFound | ManagerReservationNotAllowed | InvalidWorkingTime | RestaurantNotFound | TableNotFound |
                 DateTimeInThePast | ReservationNotInOpenTimes e) {
//            throw new RuntimeException(e);
        }


        Response response = reservationController.addReservation(1, params);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("reservation done", response.getMessage());
        assertEquals(mockReservation1, response.getData());
    }

    @Test
    public void testAddReservationFails() {
        String dateStr = "2025-10-18 19:00";
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DATETIME_FORMATTER);
        Map<String, String> params = Map.of(
                "people", "1",
                "datetime", dateStr
        );

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        try {
            doThrow(new RuntimeException("failed to reserve")).when(reserveService).reserveTable(1, 1, localDateTime);
        } catch (UserNotFound | ManagerReservationNotAllowed | InvalidWorkingTime | RestaurantNotFound | TableNotFound |
                 DateTimeInThePast | ReservationNotInOpenTimes e) {
//            throw new RuntimeException(e);
        }

        ResponseException exception = assertThrows(ResponseException.class, () ->
                reservationController.addReservation(1, params));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("failed to reserve", exception.getMessage());
    }

    @Test
    public void testCancelReservationSuccess() {
        try {
            doNothing().when(reserveService).cancelReservation(1);
        } catch (UserNotFound | ReservationNotFound | ReservationCannotBeCancelled e) {
//            throw new RuntimeException(e);
        }

        Response response = reservationController.cancelReservation(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("reservation cancelled", response.getMessage());
    }

    @Test
    public void testCancelReservationThrowsException() {
        try {
            doThrow(new RuntimeException("failed to cancel reservation")).when(reserveService).cancelReservation(1);
        } catch (UserNotFound | ReservationNotFound | ReservationCannotBeCancelled e) {
//            throw new RuntimeException(e);
        }

        ResponseException exception = assertThrows(ResponseException.class, () ->
                reservationController.cancelReservation(1));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("failed to cancel reservation", exception.getMessage());
    }
}

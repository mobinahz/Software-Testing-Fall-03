package mizdooni.model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class UserTest {
    private User user;
    private User manager;
    private User manager2;
    private Address address;
    private Address address2;
    private Table table;
    private Restaurant restaurant;
    private Restaurant restaurant2;
    private Reservation reservation1;
    private Reservation reservation2;

    @BeforeEach
    public void setup() {
        address = new Address("USA", "NYC", "123 Street");
        address2 = new Address("Iran", "Tehran", "Moj");

        manager = new User("Mamad", "nematipass", "mmdnemati@example.com", address, User.Role.manager);

        restaurant = new Restaurant(
                "Test Restaurant",
                manager,
                "Italian",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                "A popular Italian restaurant",
                address,
                "image_link.jpg"
        );

        restaurant2 = new Restaurant(
                "Symposium",
                manager,
                "England",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                "Be happy =)",
                address2,
                "Beautiful.jpg"
        );

        table = new Table(1, restaurant.getId(), 4); // Table number 1 with 4 seats
        user = new User("testuser", "password123", "test@example.com", address, User.Role.client);

        reservation1 = new Reservation(user, restaurant, table, LocalDateTime.now().minusDays(1));
        reservation2 = new Reservation(user, restaurant, table, LocalDateTime.now().plusDays(1));
    }


    @Test
    public void testAddReservation() {
        user.addReservation(reservation1);
        List<Reservation> reservations = user.getReservations();
        assertEquals(1, reservations.size());
        assertEquals(0, reservations.getFirst().getReservationNumber());
    }

    @Test
    public void testAddMultipleReservations() {
        user.addReservation(reservation1);
        user.addReservation(reservation2);

        List<Reservation> reservations = user.getReservations();
        assertEquals(2, reservations.size());
        assertEquals(1, reservations.get(1).getReservationNumber());
    }

    @Test
    public void testCheckReserved() {
        user.addReservation(reservation1);
        assertTrue(user.checkReserved(restaurant));
    }

    @Test
    public void testCancelReservation() {
        user.addReservation(reservation1);
        reservation1.cancel();
        assertFalse(user.checkReserved(restaurant));
    }


    @Test
    public void testReservationIsBeforeNow() {     ////????why not future
        user.addReservation(reservation2);
        assertFalse(user.checkReserved(restaurant));
    }

    @Test
    public void testEqualRestaurants() {
        user.addReservation(reservation1);
        assertFalse(user.checkReserved(restaurant2));
    }

    @Test
    public void testGetReservationByNumber() {
        user.addReservation(reservation1);
        user.addReservation(reservation2);

        Reservation result = user.getReservation(1);
        assertNotNull(result);
        assertEquals(reservation2, result);
    }

    @Test
    public void testGetCancelledReservation() {
        user.addReservation(reservation1);
        user.addReservation(reservation2);
        reservation1.cancel();
        Reservation result = user.getReservation(0);
        assertNull(result);
    }

    @Test
    public void testGetReservationInvalidNumber() {
        user.addReservation(reservation1);
        Reservation result = user.getReservation(5);
        assertNull(result);
    }

    @Test
    public void testCheckPassword() {
        assertTrue(user.checkPassword("password123"));
        assertFalse(user.checkPassword("wrongpassword"));
        assertFalse(user.checkPassword("PASSWORD123"));
        assertFalse(user.checkPassword(""));
    }

    @Test
    public void testCorrectPassword() {
        assertTrue(user.checkPassword("password123"));
    }

    @Test
    public void testIncorrectPassword() {
        assertFalse(user.checkPassword("wrongpassword"));
    }

    @Test
    public void testCheckCaseSensitivePassword() {
        assertFalse(user.checkPassword("PASSWORD123"));
    }

    @Test
    public void testEmptyPassword() {
        assertFalse(user.checkPassword(""));
    }

}

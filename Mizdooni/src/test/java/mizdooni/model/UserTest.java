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
    private Address address;
    private Table table;
    private Restaurant restaurant;
    private Reservation reservation1;
    private Reservation reservation2;

    @BeforeEach
    public void setup() {
        address = new Address("USA", "City", "123 Street");

        // Create a manager for the restaurant
        manager = new User("manageruser", "managerpass", "manager@example.com", address, User.Role.manager);

        // Create a restaurant with required parameters
        restaurant = new Restaurant(
                "Test Restaurant",
                manager,
                "Italian",
                LocalTime.of(9, 0),   // Start time: 09:00 AM
                LocalTime.of(22, 0),  // End time: 10:00 PM
                "A popular Italian restaurant",
                address,
                "image_link.jpg"
        );

        table = new Table(1, restaurant.getId(), 4); // Table number 1 with 4 seats
        user = new User("testuser", "password123", "test@example.com", address, User.Role.client);

        reservation1 = new Reservation(user, restaurant, table, LocalDateTime.now().minusDays(1));  // Past reservation
        reservation2 = new Reservation(user, restaurant, table, LocalDateTime.now().plusDays(1));   // Future reservation
    }


    @Test
    public void testAddReservation() {
        user.addReservation(reservation1);
        List<Reservation> reservations = user.getReservations();
        assertEquals(1, reservations.size());
        assertEquals(0, reservations.getFirst().getReservationNumber()); // First reservation number should be 0
    }
}

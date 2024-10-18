package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TableTest {
    private Table table;
    private Reservation reservation1;
    private Reservation reservation2;

    @BeforeEach
    void setUp() {

        table = new Table(1, 1, 4);
        Address address = new Address("Iran", "Tehran", "Nelson-Mandela");
        User user = new User("mobina", "12345", "mobinahz@gmail.com", null, User.Role.client);
        User user2 = new User("mamad", "23456", "mamad@gmail.com", null, User.Role.client);

        Restaurant restaurant = new Restaurant(
                "ChapChap",
                user,
                "Asian",
                LocalTime.of(10, 0),
                LocalTime.of(23, 0),
                "Yummy Foods",
                address,
                "image1.jpg"
        );

        reservation1 = new Reservation(user, restaurant, table, LocalDateTime.of(2024, 10, 18, 19, 0));
        reservation2 = new Reservation(user2, restaurant, table, LocalDateTime.of(2024, 10, 18, 19, 0));
    }

    @Test
    void testAddReservation() {
        table.addReservation(reservation1);

        List<Reservation> reservations = table.getReservations();
        assertEquals(1, reservations.size());
        assertEquals(reservation1, reservations.getFirst());
    }

    @Test
    void testIsReservedAtExactTime() {
        table.addReservation(reservation1);

        assertTrue(table.isReserved(LocalDateTime.of(2024, 10, 18, 19, 0)));
    }

    @Test
    void testIsReservedAtDifferentTime() {
        table.addReservation(reservation1);

        assertFalse(table.isReserved(LocalDateTime.of(2024, 10, 18, 23, 0)));
    }

    @Test
    void testIsReservedCancelled() {
        table.addReservation(reservation1);
        reservation1.cancel();

        assertFalse(table.isReserved(reservation1.getDateTime()));
    }

    @Test
    void testReservingReservedTable() {
        table.addReservation(reservation1);
        table.addReservation(reservation2);

        assertTrue(table.isReserved(reservation1.getDateTime()));
        assertFalse(table.isReserved(reservation2.getDateTime()));
    }
}

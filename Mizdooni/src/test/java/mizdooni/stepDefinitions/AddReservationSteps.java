package mizdooni.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import mizdooni.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AddReservationSteps {

    private static Map<String, User> users = new HashMap<>();

    private Map<String, Restaurant> restaurants = new HashMap<>();
    private Reservation reservation;
    private Exception exception;


    public AddReservationSteps() {
        restaurants.put("Downtown Burger", new Restaurant("Downtown Burger", new User("adminDB", "password", "admin@example.com", new Address("Iran", "Tehran", "Jordan"), User.Role.manager), "Burger", LocalTime.parse("12:00"), LocalTime.parse("23:30"), "The best burger in town", new Address("Iran", "Tehran", "Jordan"), null));
        restaurants.put("Perperook", new Restaurant("Perperook", new User("adminP", "password", "admin@example.com", new Address("Iran", "Tehran", "Gheytarieh"), User.Role.manager), "Pizza", LocalTime.parse("13:00"), LocalTime.parse("23:00"), "The best pizza in Tehran", new Address("Iran", "Tehran", "Gheytarieh"), null));
    }

    @Given("a user with username {string} and role {string} exists")
    public void aUserExists(String username, String role) {
        User.Role userRole = User.Role.valueOf(role);
        User user = new User(username, "password", "email@example.com", new Address("Iran", "Tehran", "Default Street"), userRole);
        users.put(username, user);
        System.out.println("Added user: " + username );
    }

    @When("the user adds a reservation for {string} on {string}")
    public void userAddsReservation(String restaurantName, String dateTime) {
        try {
            Reservation newReservation = createReservation(restaurantName, dateTime, users.get("JohnnyDepp"));
            users.get("JohnnyDepp").addReservation(newReservation);
            reservation = newReservation;
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("the user tries to add a reservation for {string} on {string}")
    public void userTriesToAddReservation(String restaurantName, String dateTime) {
        try {
            Reservation newReservation = createReservation(restaurantName, dateTime, users.get("JohnnyDepp"));
            users.get("JohnnyDepp").addReservation(newReservation);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the reservation is added to the user's reservation list")
    public void reservationIsAdded() {
        assertNotNull(reservation);
        assertTrue(users.get("JohnnyDepp").getReservations().contains(reservation));
    }

    @Then("the reservation number is {string}")
    public void reservationNumberIs(String expectedNumber) {
        assertNotNull(reservation);
        assertEquals(Integer.parseInt(expectedNumber), reservation.getReservationNumber());
    }

    @Then("an error message is shown: {string}")
    public void errorMessageIsShown(String errorMessage) {
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @When("the user adds another reservation for {string} on {string}")
    public void userAddsAnotherReservation(String restaurantName, String dateTime) {
        try {
            Reservation newReservation = createReservation(restaurantName, dateTime, users.get("JohnnyDepp"));
            users.get("JohnnyDepp").addReservation(newReservation);
            reservation = newReservation;
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the reservations are added to the user's reservation list")
    public void reservationsAreAdded() {
        assertEquals(2, users.get("JohnnyDepp").getReservations().size());
    }

    @Then("the reservation numbers are {string} and {string}")
    public void reservationNumbersAre(String firstNumber, String secondNumber) {
        assertEquals(Integer.parseInt(firstNumber), users.get("JohnnyDepp").getReservations().get(0).getReservationNumber());
        assertEquals(Integer.parseInt(secondNumber), users.get("JohnnyDepp").getReservations().get(1).getReservationNumber());
    }

    @When("User1 adds a reservation for {string} on {string}")
    public void user1AddsReservation(String restaurantName, String dateTime) {
        try {
            Reservation newReservation = createReservation(restaurantName, dateTime, users.get("User1"));
            users.get("User1").addReservation(newReservation);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("User2 adds a reservation for {string} on {string}")
    public void user2AddsReservation(String restaurantName, String dateTime) {
        try {
            Reservation newReservation = createReservation(restaurantName, dateTime, users.get("User2"));
            users.get("User2").addReservation(newReservation);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("both reservations are added to their respective reservation lists")
    public void bothReservationsAreAdded() {
        assertEquals(1, users.get("User1").getReservations().size());
        assertEquals(1, users.get("User2").getReservations().size());
    }

    @Then("the reservation numbers for User1 and User2 are {string} and {string}")
    public void theReservationNumbersForUser1AndUser2Are(String expectedNumberUser1, String expectedNumberUser2) {
        // Validate reservation number for User1
        Reservation reservationUser1 = users.get("User1").getReservations().getFirst();
        assertEquals(Integer.parseInt(expectedNumberUser1), reservationUser1.getReservationNumber());

        // Validate reservation number for User2
        Reservation reservationUser2 = users.get("User2").getReservations().getFirst();
        assertEquals(Integer.parseInt(expectedNumberUser2), reservationUser2.getReservationNumber());
    }


    @When("the user tries to add a reservation for {string} with no date")
    public void theUserTriesToAddAReservationForWithNoDate(String restaurantName) {
        try {
            Reservation newReservation = createReservation(restaurantName, null, users.get("JohnnyDepp"));
            users.get("JohnnyDepp").addReservation(newReservation);
        } catch (Exception e) {
            exception = e;
        }
    }


    private Reservation createReservation(String restaurantName, String dateTime, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!restaurants.containsKey(restaurantName)) {
            throw new IllegalArgumentException("Restaurant does not exist");
        }

        Restaurant restaurant = restaurants.get(restaurantName);

        if (dateTime == null) {
            throw new IllegalArgumentException("Reservation date is required");
        }

        LocalDateTime reservationDateTime = LocalDateTime.parse(dateTime);

        if (reservationDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot reserve for a past date");
        }

        LocalTime time = reservationDateTime.toLocalTime();
        if (time.isBefore(restaurant.getStartTime()) || time.isAfter(restaurant.getEndTime())) {
            throw new IllegalArgumentException("Restaurant is closed at this time");
        }

        if (user.getRole() != User.Role.client) {
            throw new IllegalArgumentException("Only clients can add reservations");
        }

        return new Reservation(user, restaurant, null, reservationDateTime);
    }


}


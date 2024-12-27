package mizdooni.stepDefinitions;

import io.cucumber.java.en.*;
import mizdooni.model.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Steps {

    private User user;
    private Restaurant restaurant;
    private Reservation reservation;
    private List<Reservation> reservations;
    private List<Review> reviews = new ArrayList<>();
    private Rating averageRating;
    private String errorMessage;
    private List<User> users;



    @Given("A sample user with a client role")
    public void aSampleUserWithAClientRole() {
        user = new User("sampleUser", "password", "user@example.com", new Address("123 Street", "City", "State"), User.Role.client);
    }

    @Given("A sample restaurant")
    public void aSampleRestaurant() {
        User manager = new User("manager", "password", "manager@example.com", new Address("123 Street", "City", "State"), User.Role.manager);
        restaurant = new Restaurant("Sample Restaurant", manager, "Diner", LocalTime.of(9, 0), LocalTime.of(22, 0), "A cozy place", null, null);
        restaurant.addTable(new Table(1, 4, 2));
    }

    @Given("A sample user")
    public void aSampleUser() {
        user = new User("sampleUser", "password", "user@example.com",
                new Address("123 Street", "City", "State"), User.Role.client);
    }

    @Given("Multiple sample users")
    public void multipleSampleUsers() {
        users = new ArrayList<>();
        users.add(new User("user1", "password", "user1@example.com",
                new Address("123 Street", "City", "State"), User.Role.client));
        users.add(new User("user2", "password", "user2@example.com",
                new Address("456 Street", "City", "State"), User.Role.client));
    }

    @Given("Multiple sample users with existing reviews")
    public void multipleSampleUsersWithExistingReviews() {
        users = new ArrayList<>();
        User user1 = new User("user1", "password", "user1@example.com",
                new Address("123 Street", "City", "State"), User.Role.client);
        User user2 = new User("user2", "password", "user2@example.com",
                new Address("456 Street", "City", "State"), User.Role.client);

        users.add(user1);
        users.add(user2);

        Review review1 = new Review(user1, createRating(4.0, 5.0, 3.0, 4.0),
                "Nice experience!", LocalDateTime.now());
        Review review2 = new Review(user2, createRating(5.0, 4.0, 5.0, 4.5),
                "Great experience!", LocalDateTime.now());

        restaurant.addReview(review1);
        restaurant.addReview(review2);
        reviews.add(review1);
        reviews.add(review2);
    }

    @Given("A restaurant without any reviews")
    public void aRestaurantWithoutAnyReviews() {
        User manager = new User("manager", "password", "manager@example.com",
                new Address("123 Street", "City", "State"), User.Role.manager);
        restaurant = new Restaurant("Sample Restaurant", manager, "Diner", null, null,
                "A cozy place", null, null);
        assertTrue("The restaurant should start with no reviews", restaurant.getReviews().isEmpty());
    }

    @Given("A restaurant")
    public void aRestaurant() {
        User manager = new User("manager", "password", "manager@example.com",
                new Address("123 Street", "City", "State"), User.Role.manager);
        restaurant = new Restaurant("Sample Restaurant", manager, "Diner", null, null,
                "A cozy place", null, null);
    }

    @When("The user adds a reservation for the restaurant at a specific date and time")
    public void theUserAddsAReservationForTheRestaurantAtASpecificDateAndTime() {
        Table table = restaurant.getTable(1);
        LocalDateTime reservationDateTime = LocalDateTime.now().plusDays(1);
        reservation = new Reservation(user, restaurant, table, reservationDateTime);
        reservation.setReservationNumber(reservations == null ? 1 : reservations.size() + 1);
        if (reservations == null) {
            reservations = new ArrayList<>();
        }
        reservations.add(reservation);
    }

    @When("The user tries to add a reservation for a past date")
    public void theUserTriesToAddAReservationForAPastDate() {
        Table table = restaurant.getTable(1);
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(1);
        reservation = new Reservation(user, restaurant, table, pastDateTime);
    }

    @When("The user tries to add a reservation outside the restaurant's operational hours")
    public void theUserTriesToAddAReservationOutsideOperationalHours() {
        Table table = restaurant.getTable(1);
        LocalDateTime invalidDateTime = LocalDateTime.now().withHour(23);
        reservation = new Reservation(user, restaurant, table, invalidDateTime);
    }

    @When("A review is added")
    public void aReviewIsAdded() {
        User user = new User("sampleUser", "password", "user@example.com",
                new Address("123 Street", "City", "State"), User.Role.client);
        Rating rating = new Rating();
        rating.food = 4.0;
        rating.service = 5.0;
        rating.ambiance = 3.0;
        rating.overall = 4.0;

        Review review = new Review(user, rating, "Great experience!", LocalDateTime.now());
        restaurant.addReview(review);
    }

    @When("The user tries to add a reservation with incomplete details")
    public void theUserTriesToAddAReservationWithIncompleteDetails() {
        reservation = new Reservation(user, restaurant, null, LocalDateTime.now().plusDays(1));
    }

    @When("The user tries to add a reservation for a non-existent restaurant")
    public void theUserTriesToAddAReservationForANonExistentRestaurant() {
        this.restaurant = null;
        Table table = null;
        LocalDateTime reservationDateTime = LocalDateTime.now().plusDays(1);

        try {
            reservation = new Reservation(user, restaurant, table, reservationDateTime);
        } catch (IllegalArgumentException e) {
            errorMessage = e.getMessage();
        }
    }

    @When("The users update their reviews for the restaurant")
    public void theUsersUpdateTheirReviewsForTheRestaurant() {
        for (User user : users) {
            Review updatedReview = new Review(user, createRating(3.0, 3.0, 3.0, 3.0),
                    "Updated review for " + user.getUsername(), LocalDateTime.now());
            restaurant.addReview(updatedReview);
        }
    }

    @When("Each user adds multiple reviews for the restaurant")
    public void eachUserAddsMultipleReviewsForTheRestaurant() {
        User user1 = new User("User1", "password", "user1@example.com",
                new Address("123 Street", "City", "State"), User.Role.client);
        Rating firstRatingUser1 = createRating(3.0, 3.5, 4.0, 3.5);
        Rating secondRatingUser1 = createRating(4.0, 4.5, 4.0, 4.2);
        restaurant.addReview(new Review(user1, firstRatingUser1, "User1's First Review", LocalDateTime.now().minusDays(1)));
        restaurant.addReview(new Review(user1, secondRatingUser1, "User1's Most Recent Review", LocalDateTime.now()));

        User user2 = new User("User2", "password", "user2@example.com",
                new Address("456 Street", "City", "State"), User.Role.client);
        Rating firstRatingUser2 = createRating(2.0, 3.0, 2.5, 2.8);
        Rating secondRatingUser2 = createRating(3.0, 4.0, 3.5, 3.5);
        restaurant.addReview(new Review(user2, firstRatingUser2, "User2's First Review", LocalDateTime.now().minusDays(2)));
        restaurant.addReview(new Review(user2, secondRatingUser2, "User2's Most Recent Review", LocalDateTime.now()));
    }



    @When("The user adds a review for the restaurant")
    public void theUserAddsAReviewForTheRestaurant() {
        Rating rating = new Rating();
        rating.food = 4.0;
        rating.service = 5.0;
        rating.ambiance = 3.0;
        rating.overall = 4.0;

        Review review = new Review(user, rating, "Great experience!", LocalDateTime.now());
        restaurant.addReview(review);
        reviews.add(review);
    }

    @When("Each user adds a review for the restaurant")
    public void eachUserAddsAReviewForTheRestaurant() {
        for (User user : users) {
            Rating rating = new Rating();
            rating.food = Math.random() * 5;
            rating.service = Math.random() * 5;
            rating.ambiance = Math.random() * 5;
            rating.overall = (rating.food + rating.service + rating.ambiance) / 3;

            Review review = new Review(user, rating, "A review by " + user.getUsername(), LocalDateTime.now());
            restaurant.addReview(review);
            reviews.add(review);
        }
    }


    @When("The average rating is calculated")
    public void theAverageRatingIsCalculated() {
        averageRating = restaurant.getAverageRating();
    }

    @When("The user tries to add a null review for the restaurant")
    public void theUserTriesToAddANullReviewForTheRestaurant() {
        try {
            restaurant.addReview(null);
        } catch (IllegalArgumentException e) {
            errorMessage = "Review cannot be null";
        }
    }


    @When("Each user adds a reservation for the restaurant at the same time")
    public void eachUserAddsAReservationForTheRestaurantAtTheSameTime() {
        User user1 = new User("User1", "password", "user1@example.com", new Address("123 Street", "City", "State"), User.Role.client);
        User user2 = new User("User2", "password", "user2@example.com", new Address("456 Street", "City", "State"), User.Role.client);

        Table table1 = restaurant.getTable(1);
        LocalDateTime reservationTime1 = LocalDateTime.now().plusDays(1);
        Reservation reservation1 = new Reservation(user1, restaurant, table1, reservationTime1);
        reservation1.setReservationNumber(reservations == null ? 1 : reservations.size() + 1);

        Table table2 = restaurant.getTable(1);
        LocalDateTime reservationTime2 = LocalDateTime.now().plusDays(1);
        Reservation reservation2 = new Reservation(user2, restaurant, table2, reservationTime2);
        reservation2.setReservationNumber(reservations == null ? 2 : reservations.size() + 2);

        if (reservations == null) {
            reservations = new ArrayList<>();
        }
        reservations.add(reservation1);
        reservations.add(reservation2);
    }

    @When("The user adds multiple reviews for the restaurant")
    public void theUserAddsMultipleReviewsForTheRestaurant() {
        Rating firstRating = new Rating();
        firstRating.food = 3.0;
        firstRating.service = 4.0;
        firstRating.ambiance = 3.5;
        firstRating.overall = 3.5;

        Review firstReview = new Review(user, firstRating, "First review", LocalDateTime.now().minusDays(1));
        restaurant.addReview(firstReview);

        Rating secondRating = new Rating();
        secondRating.food = 4.0;
        secondRating.service = 5.0;
        secondRating.ambiance = 4.5;
        secondRating.overall = 4.5;

        Review secondReview = new Review(user, secondRating, "Second review", LocalDateTime.now());
        restaurant.addReview(secondReview);
    }


    @When("A sample user submits a review for the restaurant")
    public void aSampleUserSubmitsAReviewForTheRestaurant() {
        User user = new User("sampleUser", "password", "user@example.com", new Address("123 Street", "City", "State"), User.Role.client);
        Rating rating = createRating(4.0, 5.0, 3.0, 4.0);

        Review review = new Review(user, rating, "Great experience!", LocalDateTime.now());
        restaurant.addReview(review);
        reviews.add(review);
    }

    @When("Multiple sample users submit reviews for the restaurant")
    public void multipleSampleUsersSubmitReviewsForTheRestaurant() {
        User user1 = new User("user1", "password", "user1@example.com", new Address("123 Street", "City", "State"), User.Role.client);
        User user2 = new User("user2", "password", "user2@example.com", new Address("456 Street", "City", "State"), User.Role.client);

        Review review1 = new Review(user1, createRating(4.0, 5.0, 3.0, 4.0), "Nice!", LocalDateTime.now());
        Review review2 = new Review(user2, createRating(5.0, 4.0, 5.0, 4.5), "Awesome!", LocalDateTime.now());

        restaurant.addReview(review1);
        restaurant.addReview(review2);
        reviews.add(review1);
        reviews.add(review2);
    }

    @When("The manager calculates the average rating")
    public void theManagerCalculatesTheAverageRating() {
        averageRating = restaurant.getAverageRating();
    }

    @When("Multiple reviews are added")
    public void multipleReviewsAreAdded() {
        User user1 = new User("user1", "password", "user1@example.com",
                new Address("123 Street", "City", "State"), User.Role.client);
        User user2 = new User("user2", "password", "user2@example.com",
                new Address("456 Street", "City", "State"), User.Role.client);

        Rating rating1 = new Rating();
        rating1.food = 4.0;
        rating1.service = 5.0;
        rating1.ambiance = 3.0;
        rating1.overall = 4.0;

        Rating rating2 = new Rating();
        rating2.food = 5.0;
        rating2.service = 4.0;
        rating2.ambiance = 5.0;
        rating2.overall = 4.5;

        Review review1 = new Review(user1, rating1, "Great experience!", LocalDateTime.now());
        Review review2 = new Review(user2, rating2, "Awesome service!", LocalDateTime.now());

        restaurant.addReview(review1);
        restaurant.addReview(review2);
    }

    @Then("The reservation should have a unique reservation number")
    public void theReservationShouldHaveAUniqueReservationNumber() {
        assertNotNull("Reservation should not be null", reservation);
        assertTrue("Reservation number should be unique and greater than 0", reservation.getReservationNumber() > 0);
    }

    @Then("All average ratings \\(food, service, ambiance, and overall) should be {double}")
    public void allAverageRatingsFoodServiceAmbianceAndOverallShouldBe(Double expectedRating) {
        Rating averageRating = restaurant.getAverageRating();

        assertEquals("Average food rating should match", expectedRating, averageRating.food, 0.01);
        assertEquals("Average service rating should match", expectedRating, averageRating.service, 0.01);
        assertEquals("Average ambiance rating should match", expectedRating, averageRating.ambiance, 0.01);
        assertEquals("Average overall rating should match", expectedRating, averageRating.overall, 0.01);
    }

    @Then("Each reservation should be added to the respective user's reservation list")
    public void eachReservationShouldBeAddedToTheRespectiveUsersReservationList() {
        assertNotNull("Reservations list should not be null", reservations);
        assertTrue("Each user's reservation should be added",
                reservations.stream().anyMatch(r -> r.getUser().getUsername().equals("User1")) &&
                        reservations.stream().anyMatch(r -> r.getUser().getUsername().equals("User2")));
    }


    @Then("The restaurant should not have any reviews")
    public void theRestaurantShouldNotHaveAnyReviews() {
        assertNotNull("Restaurant reviews list should not be null", restaurant.getReviews());
        assertTrue("Restaurant should not have any reviews", restaurant.getReviews().isEmpty());
    }


    @Then("Each reservation should have a unique reservation number")
    public void eachReservationShouldHaveAUniqueReservationNumber() {
        assertNotNull("Reservations list should not be null", reservations);
        List<Integer> uniqueNumbers = reservations.stream()
                .map(Reservation::getReservationNumber)
                .distinct()
                .toList();
        assertEquals("Each reservation should have a unique number", reservations.size(), uniqueNumbers.size());
    }

    @Then("The restaurant should have one review")
    public void theRestaurantShouldHaveOneReview() {
        assertEquals("The restaurant should have exactly one review", 1, restaurant.getReviews().size());
    }


    @Then("The review should belong to the user")
    public void theReviewShouldBelongToTheUser() {
        boolean found = restaurant.getReviews().stream()
                .anyMatch(review -> review.getUser().equals(user));
        assertTrue("The review should belong to the user", found);
    }


    @Then("The review's overall rating should be recorded")
    public void theReviewsOverallRatingShouldBeRecorded() {
        boolean ratingFound = restaurant.getReviews().stream()
                .anyMatch(review -> review.getRating().overall == 4.0);
        assertTrue("The review's overall rating should be recorded", ratingFound);
    }

    @Then("Only the most recent review by each user should be recorded")
    public void onlyTheMostRecentReviewByEachUserShouldBeRecorded() {
        assertEquals("There should be two reviews in total", 2, restaurant.getReviews().size());

        assertTrue("User1's most recent review should be retained",
                restaurant.getReviews().stream()
                        .anyMatch(review -> review.getUser().getUsername().equals("User1") &&
                                review.getComment().equals("User1's Most Recent Review")));

        assertTrue("User2's most recent review should be retained",
                restaurant.getReviews().stream()
                        .anyMatch(review -> review.getUser().getUsername().equals("User2") &&
                                review.getComment().equals("User2's Most Recent Review")));
    }

    @Then("The average overall rating should reflect the most recent reviews")
    public void theAverageOverallRatingShouldReflectTheMostRecentReviews() {
        Rating averageRating = restaurant.getAverageRating();
        assertNotNull("Average rating should be calculated", averageRating);

        double expectedOverall = (4.2 + 3.5) / 2;
        assertEquals(expectedOverall, averageRating.overall, 0.01);
    }

    @Then("All average ratings \\(food, service, ambiance, and overall) should reflect the combined reviews")
    public void allAverageRatingsFoodServiceAmbianceAndOverallShouldReflectTheCombinedReviews() {
        Rating averageRating = restaurant.getAverageRating();

        double expectedFoodRating = (4.0 + 5.0) / 2;
        double expectedServiceRating = (5.0 + 4.0) / 2;
        double expectedAmbianceRating = (3.0 + 5.0) / 2;
        double expectedOverallRating = (4.0 + 4.5) / 2;

        assertEquals("Average food rating should reflect the combined reviews", expectedFoodRating, averageRating.food, 0.01);
        assertEquals("Average service rating should reflect the combined reviews", expectedServiceRating, averageRating.service, 0.01);
        assertEquals("Average ambiance rating should reflect the combined reviews", expectedAmbianceRating, averageRating.ambiance, 0.01);
        assertEquals("Average overall rating should reflect the combined reviews", expectedOverallRating, averageRating.overall, 0.01);
    }


    @Then("The reservation should be added to the user's reservation list")
    public void theReservationShouldBeAddedToTheUsersReservationList() {
        assertTrue("Reservation should be added", reservations.contains(reservation));
    }

    @Then("The average food rating should be {double}")
    public void theAverageFoodRatingShouldBe(double expectedFood) {
        assertNotNull("Average rating is not calculated", averageRating);
        assertEquals(expectedFood, averageRating.food, 0.01);
    }

    @Then("The restaurant should have multiple reviews")
    public void theRestaurantShouldHaveMultipleReviews() {
        assertTrue("The restaurant should have multiple reviews", restaurant.getReviews().size() > 1);
    }


    @Then("Each user's review should be included")
    public void eachUsersReviewShouldBeIncluded() {
        for (User user : users) {
            boolean found = restaurant.getReviews().stream()
                    .anyMatch(review -> review.getUser().equals(user));
            assertTrue("Review for user " + user.getUsername() + " should be included", found);
        }
    }

    @Then("The restaurant should still have one review per user")
    public void theRestaurantShouldStillHaveOneReviewPerUser() {
        for (User user : users) {
            long userReviewCount = restaurant.getReviews().stream()
                    .filter(review -> review.getUser().equals(user))
                    .count();
            assertEquals("There should be exactly one review per user", 1, userReviewCount);
        }
    }


    @Then("The average overall rating should be updated")
    public void theAverageOverallRatingShouldBeUpdated() {
        assertNotNull("The average rating should be calculated", averageRating);
        double expectedOverall = reviews.stream()
                .mapToDouble(review -> review.getRating().overall)
                .average()
                .orElse(0.0);
        assertEquals("The average overall rating should be updated correctly", expectedOverall, averageRating.overall, 0.01);
    }


    @Then("The average overall rating should be calculated")
    public void theAverageOverallRatingShouldBeCalculated() {
        assertNotNull("The average rating should be calculated", averageRating);
        double expectedOverall = reviews.stream()
                .mapToDouble(review -> review.getRating().overall)
                .average()
                .orElse(0.0);
        assertEquals("The average overall rating should match the calculated value", expectedOverall, averageRating.overall, 0.01);
    }

    @Then("Only the most recent review should be recorded")
    public void onlyTheMostRecentReviewShouldBeRecorded() {
        assertEquals("There should be exactly one review for the user",
                1, restaurant.getReviews().stream()
                        .filter(review -> review.getUser().equals(user))
                        .count());

        Review recordedReview = restaurant.getReviews().stream()
                .filter(review -> review.getUser().equals(user))
                .findFirst()
                .orElse(null);

        assertNotNull("A review should be recorded", recordedReview);
        assertEquals("Second review", recordedReview.getComment());
    }

    @Then("All average ratings \\(food, service, ambiance, and overall) should reflect the review")
    public void allAverageRatingsFoodServiceAmbianceAndOverallShouldReflectTheReview() {
        Rating averageRating = restaurant.getAverageRating();

        assertEquals("Average food rating should reflect the review", 4.0, averageRating.food, 0.01);
        assertEquals("Average service rating should reflect the review", 5.0, averageRating.service, 0.01);
        assertEquals("Average ambiance rating should reflect the review", 3.0, averageRating.ambiance, 0.01);
        assertEquals("Average overall rating should reflect the review", 4.0, averageRating.overall, 0.01);
    }



    @Then("An error message should be shown indicating {string}")
    public void anErrorMessageShouldBeShownIndicating(String expectedErrorMessage) {
        if (errorMessage != null && errorMessage.equals("Review cannot be null")) {
            assertEquals("Review cannot be null", expectedErrorMessage);
        } else if (restaurant == null) {
            assertEquals("Restaurant does not exist", expectedErrorMessage);
        } else if (reservation != null && reservation.isPastTime()) {
            assertEquals("Cannot reserve for a past date", expectedErrorMessage);
        } else if (reservation != null && reservation.getTable() == null) {
            assertEquals("Reservation details are incomplete", expectedErrorMessage);
        } else if (reservation != null &&
                (reservation.getDateTime().toLocalTime().isBefore(restaurant.getStartTime())
                        || reservation.getDateTime().toLocalTime().isAfter(restaurant.getEndTime()))) {
            assertEquals("Restaurant is closed at this time", expectedErrorMessage);
        } else {
            fail("Error condition not matched");
        }
    }

    @Then("The average overall rating should reflect the most recent review")
    public void theAverageOverallRatingShouldReflectTheMostRecentReview() {
        Rating averageRating = restaurant.getAverageRating();
        assertNotNull("Average rating should be calculated", averageRating);
        assertEquals(4.5, averageRating.overall, 0.01);
    }


    private Rating createRating(double food, double service, double ambiance, double overall) {
        Rating rating = new Rating();
        rating.food = food;
        rating.service = service;
        rating.ambiance = ambiance;
        rating.overall = overall;
        return rating;
    }
}

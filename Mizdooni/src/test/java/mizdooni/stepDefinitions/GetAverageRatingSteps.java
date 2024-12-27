package mizdooni.stepDefinitions;

import io.cucumber.java.en.*;
import mizdooni.model.*;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class GetAverageRatingSteps {

    private Restaurant restaurant;
    private Rating averageRating;

    @Given("a restaurant named {string}")
    public void aRestaurantNamed(String name) {
        User manager = new User("manager", "password", "manager@example.com", new Address("123 Street", "City", "State"), User.Role.manager);
        restaurant = new Restaurant(name, manager, "General", null, null, "A sample description", null, null);
    }

    @Given("a user named {string} adds a review with food {double}, service {double}, ambiance {double}, and overall {double}")
    public void aUserAddsAReview(String username, double food, double service, double ambiance, double overall) {
        User user = new User(username, "password", username + "@example.com", new Address("123 Street", "City", "State"), User.Role.client);
        Rating rating = new Rating();
        rating.food = food;
        rating.service = service;
        rating.ambiance = ambiance;
        rating.overall = overall;
        Review review = new Review(user, rating, "Great experience!", LocalDateTime.now());
        restaurant.addReview(review);
    }

    @Given("the same user named {string} adds a review with food {double}, service {double}, ambiance {double}, and overall {double}")
    public void theSameUserNamedAddsAReviewWithFoodServiceAmbianceAndOverall(String username, double food, double service, double ambiance, double overall) {
        User user = new User(username, "password", username + "@example.com", new Address("123 Street", "City", "State"), User.Role.client);
        Rating rating = new Rating();
        rating.food = food;
        rating.service = service;
        rating.ambiance = ambiance;
        rating.overall = overall;
        Review review = new Review(user, rating, "Updated review!", LocalDateTime.now());
        restaurant.addReview(review); // This will overwrite the previous review for the same user
    }


    @When("I calculate the average rating")
    public void iCalculateTheAverageRating() {
        averageRating = restaurant.getAverageRating();
    }

    @Then("the average food rating should be {double}")
    public void theAverageFoodRatingShouldBe(double expectedFood) {
        assertEquals(expectedFood, averageRating.food, 0.01);
    }

    @Then("the average service rating should be {double}")
    public void theAverageServiceRatingShouldBe(double expectedService) {
        assertEquals(expectedService, averageRating.service, 0.01);
    }

    @Then("the average ambiance rating should be {double}")
    public void theAverageAmbianceRatingShouldBe(double expectedAmbiance) {
        assertEquals(expectedAmbiance, averageRating.ambiance, 0.01);
    }

    @Then("the average overall rating should be {double}")
    public void theAverageOverallRatingShouldBe(double expectedOverall) {
        assertEquals(expectedOverall, averageRating.overall, 0.01);
    }
}

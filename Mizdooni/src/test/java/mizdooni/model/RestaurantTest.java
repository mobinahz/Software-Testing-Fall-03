package mizdooni.model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
public class RestaurantTest {
    private User user;
    private User user2;
    private Address address;
    private Restaurant restaurant;
    private Table table1;
    private Table table2;
    private Rating rating;
    private Rating rating2;
    private Rating rating3;
    private Rating rating4;


    @BeforeEach
    public void setUp() {
        User manager = new User("mobina", "12345", "mobinahz@gmail.com", null, User.Role.manager);
        User manager2 = new User("Mamad", "nematipass", "mmdnemati@gmail.com", address, User.Role.manager);

        address = new Address("Iran", "Tehran", "Moj");
        Address address2 = new Address("Iran", "Tehran", "Nelson-Mandela");

        restaurant = new Restaurant(
                "Symposium",
                manager,
                "England",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                "Be happy =)",
                address,
                "Beautiful.jpg"
        );

        Restaurant restaurant2 = new Restaurant(
                "ChapChap",
                manager2,
                "Asian",
                LocalTime.of(10, 0),
                LocalTime.of(23, 0),
                "Yummy Foods",
                address2,
                "image2.jpg"
        );

        user = new User("aminset", "password123", "amin@gmail.com", address, User.Role.client);
        user2 = new User("nahid", "nahid1", "nahid@gmail.com", address2, User.Role.client);

        table1 = new Table(1, restaurant.getId(), 4);
        table2 = new Table(2, restaurant2.getId(), 6);

        rating = new Rating();
        rating.food = 4.5;
        rating.service = 4.0;
        rating.ambiance = 3.5;
        rating.overall = 4.2;

        rating2 = new Rating();
        rating2.food = 4.5;
        rating2.service = 4.0;
        rating2.ambiance = 3.5;
        rating2.overall = 4.2;

        rating3 = new Rating();
        rating3.food = 4;
        rating3.service = 5;
        rating3.ambiance = 3;
        rating3.overall = 4;

        rating4 = new Rating();
        rating4.food = 5;
        rating4.service = 4;
        rating4.ambiance = 4;
        rating4.overall = 5;
    }
    @Test
    public void testAddTable() {
        restaurant.addTable(table1);
        restaurant.addTable(table2);

        List<Table> tables = restaurant.getTables();
        assertEquals(2, tables.size());
        assertEquals(1, tables.get(0).getTableNumber());
        assertEquals(2, tables.get(1).getTableNumber());
    }
    @Test
    public void testGetTable() {
        restaurant.addTable(table1);

        Table result = restaurant.getTable(1);
        assertNotNull(result);

        assertEquals(4, result.getSeatsNumber());

        Table nonExistent = restaurant.getTable(2);
        assertNull(nonExistent);
    }
    @Test
    public void testGetNonExistentTable() {
        restaurant.addTable(table1);
        Table nonExistent = restaurant.getTable(2);
        assertNull(nonExistent);
    }

    @Test
    void testAddNewReview() {
        Rating rating = new Rating();
        rating.food = 4.5;
        rating.service = 4.0;
        rating.ambiance = 3.5;
        rating.overall = 4.2;

        Review review = new Review(user, rating, "Good experience", LocalDateTime.now());

        restaurant.addReview(review);
        assertEquals(1, restaurant.getReviews().size());
        assertEquals(review, restaurant.getReviews().getFirst());
    }

    @Test
    void testReplaceReview() {
        Review review1 = new Review(user, rating, "Good experience", LocalDateTime.now());
        Review review2 = new Review(user, rating2, "Not great the second time", LocalDateTime.now());

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        assertEquals(1, restaurant.getReviews().size());
        assertEquals(review2, restaurant.getReviews().getFirst());
        assertNotEquals(review1, restaurant.getReviews().getFirst());
    }
    @Test
    void testGetNoReviewsAverageRating() {
        Rating avgRating = restaurant.getAverageRating();

        assertEquals(0, avgRating.food);
        assertEquals(0, avgRating.service);
        assertEquals(0, avgRating.ambiance);
        assertEquals(0, avgRating.overall);
    }
    @Test
    void testGetAverageRatingWithReviews() {
        Review review1 = new Review(user, rating3, "Great!", LocalDateTime.now());
        Review review2 = new Review(user2, rating4, "Very nice", LocalDateTime.now());

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        Rating avgRating = restaurant.getAverageRating();

        assertEquals(4.5, avgRating.food, 0.01);
        assertEquals(4.5, avgRating.service, 0.01);
        assertEquals(3.5, avgRating.ambiance, 0.01);
        assertEquals(4.5, avgRating.overall, 0.01);
    }

    @Test
    void testGetStarCount() {
        Review review1 = new Review(user, rating3, "Great!", LocalDateTime.now());
        Review review2 = new Review(user2, rating4, "Very nice", LocalDateTime.now());

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        assertEquals(5, restaurant.getStarCount());
    }
    @Test
    void testGetMaxSeatsNumber() {
        restaurant.addTable(table1);
        restaurant.addTable(table2);

        assertEquals(6, restaurant.getMaxSeatsNumber());
    }
}

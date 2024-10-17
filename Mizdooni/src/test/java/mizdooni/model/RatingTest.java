package mizdooni.model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class RatingTest {
    @ParameterizedTest
    @CsvSource({
            "4.0, 4, 'Exact whole rating should return the same value as stars'",
            "3.4, 3, 'Rating 3.4 should round to 3 stars'",
            "3.5, 4, 'Rating 3.5 should round to 4 stars'",
            "7.5, 5, 'Rating 7.5 should be capped at 5 stars'",
            "4.999, 5, 'Rating just below 5 should round to 5 stars'",
            "0.01, 0, 'Rating just above 0 should round to 0 stars'",
            "0.0, 0, 'Rating 0.0 should result in 0 stars'",
            "4.499999, 4, 'Borderline value should round to 4 stars'",
            "4.5, 5, 'Borderline value should round to 5 stars'"
    })
    void testStarCount(double overallRating, int expectedStars, String message) {
        Rating rating = new Rating();
        rating.overall = overallRating;
        assertEquals(expectedStars, rating.getStarCount(), message);
    }
    //TODO: -1 -> 0
//    @Test
//    void testStarCountNegativeValue() {
//        Rating rating = new Rating();
//        rating.overall = -1.0;
//        assertEquals(0, rating.getStarCount(), "Negative rating should result in 0 stars");
//    }

    @Test
    void testVariousRatings() {
        Rating rating = new Rating();
        rating.food = 4.5;
        rating.service = 4.0;
        rating.ambiance = 3.5;
        rating.overall = (rating.food + rating.service + rating.ambiance) / 3;
        assertEquals(4, rating.getStarCount(), "Calculated overall rating should be 4 stars");
    }
}

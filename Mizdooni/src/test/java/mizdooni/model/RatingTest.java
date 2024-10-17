package mizdooni.model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.swing.plaf.ToolBarUI;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class RatingTest {
    @ParameterizedTest
    @CsvSource({
            "4.0, 4, 'Exact whole rating should return the same value as stars'",  // Exact whole number
            "3.4, 3, 'Rating 3.4 should round to 3 stars'",                       // Rounded down
            "3.5, 4, 'Rating 3.5 should round to 4 stars'",                       // Rounded up
            "7.5, 5, 'Rating 7.5 should be capped at 5 stars'",                 // Capped at 5
            "4.999, 5, 'Rating just below 5 should round to 5 stars'",            // Just below 5
            "0.01, 0, 'Rating just above 0 should round to 0 stars'",             // Just above 0
            "0.0, 0, 'Rating 0.0 should result in 0 stars'",                      // Zero value
            "4.499999, 4, 'Borderline value should round to 4 stars'",            // Border case 4.499999
            "4.5, 5, 'Borderline value should round to 5 stars'"                  // Border case 4.5

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

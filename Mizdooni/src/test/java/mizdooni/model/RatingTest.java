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
    @Test
    void testStarCountExactWholeNumbers() {
        Rating rating = new Rating();
        rating.overall = 4.0;
        assertEquals(4, rating.getStarCount(), "Exact whole rating should return the same value as stars");
    }
    @Test
    void testStarCountRoundedDown() {
        Rating rating = new Rating();
        rating.overall = 3.4;
        assertEquals(3, rating.getStarCount(), "Rating 3.4 should round to 3 stars");
    }
    @Test
    void testStarCountRoundedUp() {
        Rating rating = new Rating();
        rating.overall = 3.5;
        assertEquals(4, rating.getStarCount(), "Rating 3.5 should round to 4 stars");
    }
    @Test
    void testStarCountCappedAt5() {
        Rating rating = new Rating();
        rating.overall = 7.5;
        assertEquals(5, rating.getStarCount(), "Rating 6.0 should be capped at 5 stars");
    }
    //TODO: -1 -> 0
//    @Test
//    void testStarCountNegativeValue() {
//        Rating rating = new Rating();
//        rating.overall = -1.0;
//        assertEquals(0, rating.getStarCount(), "Negative rating should result in 0 stars");
//    }
    @Test
    void testStarCountZeroValue() {
        Rating rating = new Rating();
        rating.overall = 0.0;
        assertEquals(0, rating.getStarCount(), "Rating 0.0 should result in 0 stars");
    }
    @Test
    void testStarCount_BorderCase() {
        Rating rating = new Rating();
        rating.overall = 4.499999;
        assertEquals(4, rating.getStarCount(), "Borderline value should round to 4 stars");

        rating.overall = 4.5;
        assertEquals(5, rating.getStarCount(), "Borderline value should round to 5 stars");
    }
    @Test
    void testVariousRatings() {
        Rating rating = new Rating();
        rating.food = 4.5;
        rating.service = 4.0;
        rating.ambiance = 3.5;
        rating.overall = (rating.food + rating.service + rating.ambiance) / 3;
        assertEquals(4, rating.getStarCount(), "Calculated overall rating should be 4 stars");
    }
    @Test
    void testMaxBoundaryJustBelow5() {
        Rating rating = new Rating();
        rating.overall = 4.999;
        assertEquals(5, rating.getStarCount(), "Rating just below 5 should round to 5 stars");
    }
    @Test
    void testMinBoundaryJustAbove0() {
        Rating rating = new Rating();
        rating.overall = 0.01;
        assertEquals(0, rating.getStarCount(), "Rating just above 0 should round to 0 stars");
    }

}

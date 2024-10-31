package mizdooni.controllers;

import mizdooni.exceptions.*;
import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.model.Review;
import mizdooni.model.User;
import mizdooni.model.Rating;
import mizdooni.response.PagedList;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewControllerTest {

        @Mock
        private RestaurantService restaurantService;

        @Mock
        private ReviewService reviewService;

        @InjectMocks
        private ReviewController reviewController;

        private Restaurant mockRestaurant;
        private PagedList<Review> mockReviews;

    @BeforeEach
        public void setUp() {
            MockitoAnnotations.openMocks(this);
            Address address = new Address("Iran", "Tehran", "Moj");
            User manager = new User("mobina", "12345", "mobinahz@gmail.com", address, User.Role.manager);
            mockRestaurant  = new Restaurant(
                    "Symposium",
                    manager,
                    "England",
                    LocalTime.of(9, 0),
                    LocalTime.of(22, 0),
                    "Be happy =)",
                    address,
                    "Beautiful.jpg"
            );
            Review mockReview1 = new Review(manager, new Rating(), "Good", LocalDateTime.now());
            Review mockReview2 = new Review(manager, new Rating(), "Worst!", LocalDateTime.now());
            mockReviews = new PagedList<>(List.of(mockReview1, mockReview2), 1, 10);
        }

    @Test
    public void testGetReviewsWhenSuccess() {
        try(MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
            utils.when(() -> ControllerUtils.checkRestaurant(eq(1), any())).thenReturn(mockRestaurant);

            try {
                when(reviewService.getReviews(anyInt(), eq(1))).thenReturn(mockReviews);
            } catch (RestaurantNotFound e) {
//                throw new RuntimeException(e);
            }

            Response response = reviewController.getReviews(1, 1);

            assertNotNull(response);
            assertEquals("reviews for restaurant (" + 1 + "): " + mockRestaurant.getName(), response.getMessage());
            assertEquals(mockReviews, response.getData());
        }
    }

    @Test
    public void testGetReviewsWhenFails() {
        try(MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
            utils.when(() -> ControllerUtils.checkRestaurant(eq(1), any())).thenReturn(mockRestaurant);

            try {
                doThrow(new RuntimeException("failed to get reviews")).when(reviewService).getReviews(anyInt(), eq(1));
            } catch (RestaurantNotFound e) {
//                throw new RuntimeException(e);
            }

            ResponseException exception = assertThrows(ResponseException.class, () ->
                    reviewController.getReviews(1, 1));

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
            assertEquals("failed to get reviews", exception.getMessage());
        }
    }

    @Test
    public void testGetReviewsWhenRestaurantNotFound() {
        int page = 1;
        try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
            utils.when(() -> ControllerUtils.checkRestaurant(eq(1), any())).thenReturn(null);

            ResponseException exception = assertThrows(ResponseException.class, () ->
                    reviewController.getReviews(1, page));

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        }
    }

    @Test
    public void testAddReviewWhenParamsMissing() {
        Map<String, Object> incompleteParams = Map.of(
                "comment", "Missing rating" // Rating is missing
        );

        try (MockedStatic<ControllerUtils> utils = mockStatic(ControllerUtils.class)) {
            utils.when(() -> ControllerUtils.checkRestaurant(eq(1), any())).thenReturn(null);

            ResponseException exception = assertThrows(ResponseException.class, () ->
                    reviewController.addReview(1, incompleteParams));

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
            assertEquals(PARAMS_MISSING, exception.getMessage());
        }
    }

    @Test
    public void testAddReviewWhenParamsBadType() {
        Map<String, Object> badParams = Map.of(
                "comment", "Nice!",
                "rating", Map.of(
                        "food", "WORST!!!!!!!",  // Should be a number
                        "service", 4.0,
                        "ambiance", 3.5,
                        "overall", 4.2
                )
        );


        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
                reviewController.addReview(1, badParams);
            });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    public void testAddReviewWhenSuccess() {
        Map<String, Object> params = Map.of(
                "comment", "Good",
                "rating", Map.of(
                        "food", 5,
                        "service", 4,
                        "ambiance", 3,
                        "overall", 4
                )
        );

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        Response response = reviewController.addReview(1, params);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("review added successfully", response.getMessage());
    }

    @Test
    public void testAddReviewWhenFails() {
        Map<String, Object> params = Map.of(
                "comment", "Good",
                "rating", Map.of(
                        "food", 5,
                        "service", 4,
                        "ambiance", 3,
                        "overall", 4
                )
        );

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        try {
            doThrow(new RuntimeException("failed to add review")).when(reviewService).addReview(eq(1), any(), any());
        } catch (UserNotFound | ManagerCannotReview | RestaurantNotFound | InvalidReviewRating | UserHasNotReserved e) {
//            throw new RuntimeException(e);
        }
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reviewController.addReview(1, params);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("failed to add review", exception.getMessage());
    }

}

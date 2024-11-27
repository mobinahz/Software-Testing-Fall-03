package mizdooni.controllers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.*;

import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.response.PagedList;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantControllerApiTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RestaurantService restaurantService;
    @Autowired
    private ObjectMapper objectMapper;
    private Restaurant mockRestaurant;
    private Restaurant mockRestaurant2;
    private Map<String, Object> addRestaurantParams;

    @BeforeEach
    void setUp() {
        reset(restaurantService);

        Address address = new Address("country", "city", "street");
        mockRestaurant = new Restaurant(
                        "little",
                        null,
                        "italian",
                        LocalTime.of(11, 11),
                        LocalTime.of(22, 40),
                        "hmm",
                        address,
                        "url!"
                );
        mockRestaurant2 = new Restaurant(
                "self",
                null,
                "iranian",
                LocalTime.of(11, 12),
                LocalTime.of(22, 41),
                "not hmm",
                address,
                "url2!"
        );

        addRestaurantParams = new HashMap<>();
        addRestaurantParams.put("name", "khoros");
        addRestaurantParams.put("type", "sokhari");
        addRestaurantParams.put("startTime", "08:30");
        addRestaurantParams.put("endTime", "23:30");
        addRestaurantParams.put("description", "Not Bad!");
        addRestaurantParams.put("image", "url3!");
        addRestaurantParams.put("address", Map.of("country", "country", "city", "city", "street", "street"));

    }

    @Test
    void testGetRestaurantSuccess() {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        try {
            mockMvc.perform(get("/restaurants/{restaurantId}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("restaurant found"))
                    .andExpect(jsonPath("$.data.name").value("little"))
                    .andExpect(jsonPath("$.data.type").value("italian"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRestaurantNotFound() {
        doThrow(new ResponseException(HttpStatus.NOT_FOUND, "restaurant not found"))
                .when(restaurantService)
                .getRestaurant(1);

        try {
            mockMvc.perform(get("/restaurants/{restaurantId}", 1))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("restaurant not found"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRestaurantsSuccess() {
        List<Restaurant> restaurants = List.of(mockRestaurant);
        PagedList<Restaurant> pagedRestaurants = new PagedList<>(restaurants, 1, 1);

        when(restaurantService.getRestaurants(eq(1), any())).thenReturn(pagedRestaurants);

        try {
            mockMvc.perform(get("/restaurants").param("page", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("restaurants listed"))
                    .andExpect(jsonPath("$.data.page").value(1))
                    .andExpect(jsonPath("$.data.pageList[0].name").value("little"))
                    .andExpect(jsonPath("$.data.pageList[0].type").value("italian"))
                    .andExpect(jsonPath("$.data.pageList[0].address.street").value("street"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRestaurantsSuccessMultipleRestaurants() {
        List<Restaurant> restaurants = List.of(mockRestaurant, mockRestaurant2);
        PagedList<Restaurant> pagedRestaurants = new PagedList<>(restaurants, 1, 2);

        when(restaurantService.getRestaurants(eq(1), any())).thenReturn(pagedRestaurants);

        try {
            mockMvc.perform(get("/restaurants").param("page", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("restaurants listed"))
                    .andExpect(jsonPath("$.data.page").value(1))
                    .andExpect(jsonPath("$.data.pageList[0].name").value("little"))
                    .andExpect(jsonPath("$.data.pageList[0].type").value("italian"))
                    .andExpect(jsonPath("$.data.pageList[0].address.street").value("street"))
                    .andExpect(jsonPath("$.data.pageList[1].name").value("self"))
                    .andExpect(jsonPath("$.data.pageList[1].type").value("iranian"))
                    .andExpect(jsonPath("$.data.pageList[1].address.street").value("street"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRestaurantsInvalidPage() {
        List<Restaurant> restaurants = List.of(mockRestaurant, mockRestaurant2);
        PagedList<Restaurant> pagedRestaurants = new PagedList<>(restaurants, 1, 2);

        when(restaurantService.getRestaurants(eq(1), any())).thenReturn(pagedRestaurants);

        try {
            mockMvc.perform(get("/restaurants").param("page", "WRONG"))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetManagersRestaurantsSuccess() {
        List<Restaurant> restaurants = List.of(mockRestaurant);

        when(restaurantService.getManagerRestaurants(1)).thenReturn(restaurants);

        try {
            mockMvc.perform(get("/restaurants/manager/{managerId}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("manager restaurants listed"))
                    .andExpect(jsonPath("$.data[0].name").value("little"))
                    .andExpect(jsonPath("$.data[0].type").value("italian"))
                    .andExpect(jsonPath("$.data[0].address.street").value("street"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetManagersRestaurantsEmpty() {
        when(restaurantService.getManagerRestaurants(1)).thenReturn(List.of());

        try {
            mockMvc.perform(get("/restaurants/manager/{managerId}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("manager restaurants listed"))
                    .andExpect(jsonPath("$.data").isEmpty());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddRestaurantSuccess() {
        try {
            when(restaurantService.addRestaurant(
                            anyString(),
                            anyString(),
                            any(),
                            any(),
                            anyString(),
                            any(),
                            anyString()
                    )
            ).thenReturn(555);

            mockMvc.perform(post("/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addRestaurantParams))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("restaurant added"))
                    .andExpect(jsonPath("$.data").value(555));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    void testAddRestaurantInvalidParameters() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", 1313);
        params.put("type", "sokhari");
        params.put("startTime", "08:30");
        params.put("endTime", 1313);
        params.put("description", "Not Bad!");
        params.put("image", "url3!");
        params.put("address", Map.of("country", "country", "city", "city", "street", "street"));

        mockMvc.perform(post("/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(params))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("bad parameter type"));
    }

    @Test
    void testAddRestaurantMissingParameters() {
        Map<String, Object> params = new HashMap<>();

        try {
            mockMvc.perform(post("/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(params))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("parameters missing"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddRestaurantDuplicated() {
        try {
            doThrow(new ResponseException(HttpStatus.BAD_REQUEST, "duplicated restaurant name"))
                    .when(restaurantService)
                    .addRestaurant(anyString(), anyString(), any(), any(), anyString(), any(), anyString());

            mockMvc.perform(post("/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(addRestaurantParams)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("duplicated restaurant name"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddRestaurantManagerNotFound() {
        try {
            doThrow(new ResponseException(HttpStatus.NOT_FOUND, "manager not found"))
                    .when(restaurantService)
                    .addRestaurant(anyString(), anyString(), any(), any(), anyString(), any(), anyString());

            mockMvc.perform(post("/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addRestaurantParams))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("manager not found"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddRestaurantInvalidTime() {
        try {
            doThrow(new ResponseException(HttpStatus.BAD_REQUEST, "invalid available time"))
                    .when(restaurantService)
                    .addRestaurant(anyString(), anyString(), any(), any(), anyString(), any(), anyString());

            mockMvc.perform(post("/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addRestaurantParams))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("invalid available time"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testValidateRestaurantNameSuccess() {
        String name = "new little";

        when(restaurantService.restaurantExists(name)).thenReturn(false);

        try {
            mockMvc.perform(get("/validate/restaurant-name").param("data", name))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("restaurant name is available"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testValidateRestaurantNameTaken() {
        String name = "old little";

        when(restaurantService.restaurantExists(name)).thenReturn(true);

        try {
            mockMvc.perform(get("/validate/restaurant-name").param("data", name))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("restaurant name is taken"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRestaurantTypesSuccess() {
        Set<String> types = Set.of("italian", "iranian", "chinese");

        when(restaurantService.getRestaurantTypes()).thenReturn(types);

        try {
            mockMvc.perform(get("/restaurants/types"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("restaurant types"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data", containsInAnyOrder("italian", "iranian", "chinese")));
    ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRestaurantTypesFails() {
        doThrow(new ResponseException(HttpStatus.BAD_REQUEST, "get restaurant types failed"))
                .when(restaurantService)
                .getRestaurantTypes();

        try {
            mockMvc.perform(get("/restaurants/types"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("get restaurant types failed"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRestaurantLocationsSuccess() {
        Map<String, Set<String>> locations = Map.of(
                "country1", Set.of("city1", "city2"),
                "country2", Set.of("city3", "city4")
        );

        when(restaurantService.getRestaurantLocations()).thenReturn(locations);

        try {
            mockMvc.perform(get("/restaurants/locations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("restaurant locations"))
                    .andExpect(jsonPath("$.data.country1").isArray())
                    .andExpect(jsonPath("$.data.country1", containsInAnyOrder("city1", "city2")))
                    .andExpect(jsonPath("$.data.country2").isArray())
                    .andExpect(jsonPath("$.data.country2", containsInAnyOrder("city3", "city4")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRestaurantLocationsFails() {
        doThrow(new ResponseException(HttpStatus.BAD_REQUEST, "get restaurant locations failed"))
                .when(restaurantService)
                .getRestaurantLocations();

        try {
            mockMvc.perform(get("/restaurants/locations"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("get restaurant locations failed"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
package mizdooni.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.model.User;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.TableService;
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
class TableControllerApiTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RestaurantService restaurantService;
    @MockBean
    private TableService tableService;
    @Autowired
    private ObjectMapper objectMapper;
    private Restaurant mockRestaurant;
    private Map<String, String> addTableParams;

    @BeforeEach
    void setUp() {
        reset(restaurantService, tableService);

        Address address = new Address("country", "city", null);
        User manager = new User(
                "mmd",
                "password",
                "mobina@gmail.com",
                address,
                User.Role.manager
        );
        mockRestaurant =
                new Restaurant(
                        "little",
                        manager,
                        "italian",
                        LocalTime.of(11, 11),
                        LocalTime.of(22, 40),
                        "hmm",
                        address,
                        "url!"
                );

        addTableParams = new HashMap<>();
        addTableParams.put("seatsNumber", "7");
    }

    @Test
    void testGetTablesSuccess() {
        List<Table> tables = Arrays.asList(new Table(1, 4, 2),
                new Table(2, 6, 4));

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        try {
            when(tableService.getTables(1)).thenReturn(tables);
            mockMvc.perform(get("/tables/{restaurantId}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("tables listed"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].tableNumber").value(1))
                    .andExpect(jsonPath("$.data[0].seatsNumber").value(2))
                    .andExpect(jsonPath("$.data[1].tableNumber").value(2))
                    .andExpect(jsonPath("$.data[1].seatsNumber").value(4));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetTablesRestaurantNotFound() {
        doThrow(new ResponseException(HttpStatus.NOT_FOUND, "restaurant not found"))
                .when(restaurantService)
                .getRestaurant(1);

        try {
            mockMvc.perform(get("/tables/{restaurantId}", 1))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("restaurant not found"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddTableSuccess() {
        try {
            when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
            doNothing().when(tableService).addTable(1, 7);

            mockMvc.perform(post("/tables/{restaurantId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addTableParams))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("table added"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddTableMissingParameters() {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        try {
            mockMvc.perform(post("/tables/{restaurantId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
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
    void testAddTableInvalidSeatsNumber() {
        Map<String, String> params = new HashMap<>();
        params.put("seatsNumber", "WRONG");

        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        try {
            mockMvc.perform(post("/tables/{restaurantId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(params))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("bad parameter type"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddTableRestaurantNotFound() {
        doThrow(new ResponseException(HttpStatus.NOT_FOUND, "restaurant not found"))
                .when(restaurantService)
                .getRestaurant(1);

        try {
            mockMvc.perform(post("/tables/{restaurantId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addTableParams))
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("restaurant not found"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddTableUserNotManager() {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);

        try {
            doThrow(new ResponseException(HttpStatus.FORBIDDEN, "user not manager"))
                    .when(tableService)
                    .addTable(1, 7);

            mockMvc.perform(post("/tables/{restaurantId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addTableParams))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("user not manager"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testAddTableInvalidManagerRestaurant() {
        when(restaurantService.getRestaurant(1)).thenReturn(mockRestaurant);
        try {
            doThrow(new ResponseException(HttpStatus.FORBIDDEN, "invalid manager restaurant"))
                    .when(tableService)
                    .addTable(1, 7);

            mockMvc.perform(post("/tables/{restaurantId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addTableParams))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("invalid manager restaurant"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

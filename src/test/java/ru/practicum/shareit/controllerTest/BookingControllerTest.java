package ru.practicum.shareit.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    BookingDto bookingDto = BookingDto.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .itemId(1)
            .bookerId(1)
            .status(BookingStatus.WAITING)
            .build();

    BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .item(new Item())
            .booker(new User())
            .status(BookingStatus.WAITING)
            .build();

    @Test
    void createBookingTest() throws Exception {
        Mockito
                .when(bookingService.createBooking(Mockito.any(), Mockito.anyLong()))
                .thenReturn(bookingDtoResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class));
    }

    @Test
    void patchBookingTest() throws Exception {
        Mockito
                .when(bookingService.patchBooking(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(bookingDtoResponse);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class));
    }

    @Test
    void getBookingTest() throws Exception {
        Mockito
                .when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingDtoResponse);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class));
    }

    @Test
    void getUsersBooking() throws Exception {
        Mockito
                .when(bookingService.getUsersBooking(Mockito.any(),
                        Mockito.anyLong(),
                        Mockito.anyInt(),
                        Mockito.anyInt()))
                .thenReturn(List.of(bookingDtoResponse));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerBooking() throws Exception {
        Mockito
                .when(bookingService.getUsersBooking(Mockito.any(),
                        Mockito.anyLong(),
                        Mockito.anyInt(),
                        Mockito.anyInt()))
                .thenReturn(List.of(bookingDtoResponse));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

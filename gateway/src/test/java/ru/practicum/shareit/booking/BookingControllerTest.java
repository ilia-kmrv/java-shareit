package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.exception.ResourceValidationException;
import ru.practicum.shareit.util.Header;
import ru.practicum.shareit.util.Util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    @MockBean
    private final BookingClient bookingClient;

    @BeforeEach
    void setUp() {

    }

    @SneakyThrows
    @Test
    void createBooking_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long userId = 0L;
        InputBookingDto inputBookingDto = InputBookingDto.builder()
                .start(Util.now().plusMinutes(1))
                .end(Util.now().plusHours(1))
                .itemId(0L)
                .build();

        when(bookingClient.createBooking(userId, inputBookingDto)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(post("/bookings")
                        .header(Header.USER_ID, userId)
                        .content(mapper.writeValueAsString(inputBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingClient).createBooking(userId, inputBookingDto);

    }

    @SneakyThrows
    @Test
    void createBooking_whenStartInThePast_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Long userId = 0L;
        InputBookingDto inputBookingDto = InputBookingDto.builder()
                .start(Util.now().minusMinutes(1))
                .end(Util.now().plusHours(1))
                .itemId(0L)
                .build();

        mvc.perform(post("/bookings")
                        .header(Header.USER_ID, userId)
                        .content(mapper.writeValueAsString(inputBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingClient, never()).createBooking(userId, inputBookingDto);
    }

    @SneakyThrows
    @Test
    void createBooking_whenEndInThePast_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Long userId = 0L;
        InputBookingDto inputBookingDto = InputBookingDto.builder()
                .start(Util.now().plusMinutes(1))
                .end(Util.now().minusHours(1))
                .itemId(0L)
                .build();

        mvc.perform(post("/bookings")
                        .header(Header.USER_ID, userId)
                        .content(mapper.writeValueAsString(inputBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingClient, never()).createBooking(userId, inputBookingDto);
    }

    @SneakyThrows
    @Test
    void createBooking_whenItemIdIsNull_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Long userId = 0L;
        InputBookingDto inputBookingDto = InputBookingDto.builder()
                .start(Util.now().plusMinutes(1))
                .end(Util.now().plusHours(1))
                .itemId(null)
                .build();

        mvc.perform(post("/bookings")
                        .header(Header.USER_ID, userId)
                        .content(mapper.writeValueAsString(inputBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingClient, never()).createBooking(userId, inputBookingDto);
    }

    @SneakyThrows
    @Test
    void changeBookingStatus_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long ownerId = 0L;
        Long bookingId = 0L;
        Boolean approved = true;

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Header.USER_ID, ownerId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk());

        verify(bookingClient).changeBookingStatus(bookingId, ownerId, approved);
    }

    @SneakyThrows
    @Test
    void getBooking_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long userId = 0L;
        Long bookingId = 0L;
        when(bookingClient.getBooking(userId, bookingId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void getAllUserBookings_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long userId = 0L;
        String state = "ALL";
        BookingState bookingState = BookingState.ALL;
        Integer from = 0;
        Integer size = 10;

        mvc.perform(get("/bookings")
                        .header(Header.USER_ID, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(bookingClient).getAllUserBookings(userId, bookingState, from, size);
    }

    @SneakyThrows
    @Test
    void getAllUserBookings_whenFromIsNegative_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Long userId = 0L;
        String state = "CURRENT";
        Integer from = -1;
        Integer size = 10;

        mvc.perform(get("/bookings")
                        .header(Header.USER_ID, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getAllUserBookings(userId, BookingState.valueOf(state), from, size);
    }

    @SneakyThrows
    @Test
    void getAllUserBookings_whenStateIsIncorrect_thenStatusIsBadRequestAndResourceValidationExceptionThrown() {
        Long userId = 0L;
        String state = "INCORRECT";
        Integer from = 0;
        Integer size = 10;

        mvc.perform(get("/bookings")
                        .header(Header.USER_ID, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceValidationException))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllUserBookings_whenSizeIsNegative_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Long userId = 0L;
        String state = "CURRENT";
        Integer from = 1;
        Integer size = -10;

        mvc.perform(get("/bookings")
                        .header(Header.USER_ID, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getAllUserBookings(userId, BookingState.valueOf(state), from, size);
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookings_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long userId = 0L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;

        mvc.perform(get("/bookings/owner")
                        .header(Header.USER_ID, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(bookingClient).getAllOwnerBookings(userId, BookingState.valueOf(state), from, size);
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookings_whenStateIsIncorrect_thenStatusIsBadRequestAndResourceValidationExceptionThrown() {
        Long userId = 0L;
        String state = "INCORRECT";
        Integer from = 0;
        Integer size = 10;

        mvc.perform(get("/bookings/owner")
                        .header(Header.USER_ID, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceValidationException))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookings_whenFromIsNegative_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Long userId = 0L;
        String state = "CURRENT";
        Integer from = -1;
        Integer size = 10;

        mvc.perform(get("/bookings/owner")
                        .header(Header.USER_ID, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getAllOwnerBookings(userId, BookingState.valueOf(state), from, size);
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookings_whenSizeIsNegative_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Long userId = 0L;
        String state = "CURRENT";
        Integer from = 1;
        Integer size = -10;

        mvc.perform(get("/bookings/owner")
                        .header(Header.USER_ID, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getAllOwnerBookings(userId, BookingState.valueOf(state), from, size);
    }
}

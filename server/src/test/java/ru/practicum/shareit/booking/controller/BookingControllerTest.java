package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Header;
import ru.practicum.shareit.util.Util;

import static org.junit.jupiter.api.Assertions.*;
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
    private final BookingService bookingService;

    Item item;
    User user;
    Booking booking;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(0L)
                .name("item name")
                .description("item description")
                .available(true)
                .ownerId(0L)
                .requestId(0L)
                .build();

        user = User.builder()
                .id(0L)
                .name("username")
                .email("username@email.com")
                .build();

        booking = Booking.builder()
                .id(0L)
                .start(Util.now().plusMinutes(10))
                .end(Util.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @SneakyThrows
    @Test
    void createBooking_whenInvoked_thenStatusIsOkAndBookingDtoReturned() {
        Long userId = 0L;
        InputBookingDto inputBookingDto = InputBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(item.getId())
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemDto)
                .booker(userDto)
                .status(booking.getStatus())
                .build();

        when(bookingService.addBooking(inputBookingDto, userId)).thenReturn(booking);

        String result = mvc.perform(post("/bookings")
                        .header(Header.USER_ID, userId)
                        .content(mapper.writeValueAsString(inputBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDto), result);
    }

//    @SneakyThrows
//    @Test
//    void createBooking_whenStartInThePast_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
//        Long userId = 0L;
//        InputBookingDto inputBookingDto = InputBookingDto.builder()
//                .start(booking.getStart().minusHours(10))
//                .end(booking.getEnd())
//                .itemId(item.getId())
//                .build();
//
//        when(bookingService.addBooking(inputBookingDto, userId)).thenReturn(booking);
//
//        mvc.perform(post("/bookings")
//                        .header(Header.USER_ID, userId)
//                        .content(mapper.writeValueAsString(inputBookingDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        Mockito.verify(bookingService, never()).addBooking(inputBookingDto, userId);
//    }

//    @SneakyThrows
//    @Test
//    void createBooking_whenEndInThePast_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
//        Long userId = 0L;
//        InputBookingDto inputBookingDto = InputBookingDto.builder()
//                .start(booking.getStart())
//                .end(booking.getEnd().minusDays(10))
//                .itemId(item.getId())
//                .build();
//
//        when(bookingService.addBooking(inputBookingDto, userId)).thenReturn(booking);
//
//        mvc.perform(post("/bookings")
//                        .header(Header.USER_ID, userId)
//                        .content(mapper.writeValueAsString(inputBookingDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        Mockito.verify(bookingService, never()).addBooking(inputBookingDto, userId);
//    }

//    @SneakyThrows
//    @Test
//    void createBooking_whenItemIdIsNull_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
//        Long userId = 0L;
//        InputBookingDto inputBookingDto = InputBookingDto.builder()
//                .start(booking.getStart())
//                .end(booking.getEnd())
//                .itemId(null)
//                .build();
//
//        when(bookingService.addBooking(inputBookingDto, userId)).thenReturn(booking);
//
//        mvc.perform(post("/bookings")
//                        .header(Header.USER_ID, userId)
//                        .content(mapper.writeValueAsString(inputBookingDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        Mockito.verify(bookingService, never()).addBooking(inputBookingDto, userId);
//    }

    @SneakyThrows
    @Test
    void changeBookingStatus_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        Long ownerId = 0L;
        Long bookingId = 0L;
        Boolean approved = true;
        when(bookingService.updateBooking(ownerId, bookingId, approved)).thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Header.USER_ID, ownerId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk());

        verify(bookingService).updateBooking(ownerId, bookingId, approved);
    }

    @SneakyThrows
    @Test
    void getBooking_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        Long userId = 0L;
        Long bookingId = 0L;
        when(bookingService.getBookingByUserId(bookingId, userId)).thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingService).getBookingByUserId(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getAllUserBookings_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        Long userId = 0L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;

        mvc.perform(get("/bookings")
                        .header(Header.USER_ID, userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(bookingService).getAllUserBookingsByState(userId, state, from, size);
    }

//    @SneakyThrows
//    @Test
//    void getAllUserBookings_whenFromIsNegative_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
//        Long userId = 0L;
//        String state = "CURRENT";
//        Integer from = -1;
//        Integer size = 10;
//
//        mvc.perform(get("/bookings")
//                        .header(Header.USER_ID, userId)
//                        .param("state", state)
//                        .param("from", String.valueOf(from))
//                        .param("size", String.valueOf(size)))
//                .andExpect(status().isBadRequest());
//
//        verify(bookingService, never()).getAllUserBookingsByState(userId, state, from, size);
//    }

//    @SneakyThrows
//    @Test
//    void getAllUserBookings_whenSizeIsNegative_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
//        Long userId = 0L;
//        String state = "CURRENT";
//        Integer from = 1;
//        Integer size = -10;
//
//        mvc.perform(get("/bookings")
//                        .header(Header.USER_ID, userId)
//                        .param("state", state)
//                        .param("from", String.valueOf(from))
//                        .param("size", String.valueOf(size)))
//                .andExpect(status().isBadRequest());
//
//        verify(bookingService, never()).getAllUserBookingsByState(userId, state, from, size);
//    }

    @SneakyThrows
    @Test
    void getAllOwnerBookings_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
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

        verify(bookingService).getAllOwnerBookingsByState(userId, state, from, size);
    }

//    @SneakyThrows
//    @Test
//    void getAllOwnerBookings_whenFromIsNegative_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
//        Long userId = 0L;
//        String state = "CURRENT";
//        Integer from = -1;
//        Integer size = 10;
//
//        mvc.perform(get("/bookings/owner")
//                        .header(Header.USER_ID, userId)
//                        .param("state", state)
//                        .param("from", String.valueOf(from))
//                        .param("size", String.valueOf(size)))
//                .andExpect(status().isBadRequest());
//
//        verify(bookingService, never()).getAllOwnerBookingsByState(userId, state, from, size);
//    }
//
//    @SneakyThrows
//    @Test
//    void getAllOwnerBookings_whenSizeIsNegative_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
//        Long userId = 0L;
//        String state = "CURRENT";
//        Integer from = 1;
//        Integer size = -10;
//
//        mvc.perform(get("/bookings/owner")
//                        .header(Header.USER_ID, userId)
//                        .param("state", state)
//                        .param("from", String.valueOf(from))
//                        .param("size", String.valueOf(size)))
//                .andExpect(status().isBadRequest());
//
//        verify(bookingService, never()).getAllOwnerBookingsByState(userId, state, from, size);
//    }
}

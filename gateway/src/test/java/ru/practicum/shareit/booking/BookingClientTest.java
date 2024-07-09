package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.InputBookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(BookingClient.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingClientTest {

    private final BookingClient bookingClient;
    private final MockRestServiceServer server;
    private final ObjectMapper mapper;

    @Value("${shareit-server.url}")
    private String serverUrl;

    String bookingDtoString;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        LocalDateTime date = LocalDateTime.of(2024, 7, 9, 21, 16);
        bookingDtoString = mapper.writeValueAsString(InputBookingDto.builder()
                .id(0L)
                .start(date)
                .end(date.plusHours(2))
                .build());
    }

    @Test
    void createBooking_whenInvoked_thenStatusIsOk() {
        this.server.expect(requestTo(serverUrl + "/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(bookingDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.bookingClient.createBooking(anyLong(), any());

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void changeBookingStatus_whenInvoked_thenStatusIsOk() {
        this.server.expect(requestTo(serverUrl + "/bookings/0?approved=false"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess(bookingDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.bookingClient.changeBookingStatus(anyLong(), anyLong(), anyBoolean());

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getBooking_whenInvoked_thenStatusIsOk() {
        this.server.expect(requestTo(serverUrl + "/bookings/0"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(bookingDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.bookingClient.getBooking(anyLong(), anyLong());

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getAllUserBookings_whenInvoked_thenStatusIsOk() {
        long userId = 0L;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 10;
        this.server.expect(requestTo(serverUrl + "/bookings?state=ALL&from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());

        ResponseEntity dto = this.bookingClient.getAllUserBookings(userId, state, from, size);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getAllOwnerBookings() {
        long userId = 0L;
        BookingState state = BookingState.ALL;
        int from = 0;
        int size = 10;
        this.server.expect(requestTo(serverUrl + "/bookings/owner?state=ALL&from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());

        ResponseEntity dto = this.bookingClient.getAllOwnerBookings(userId, state, from, size);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }
}
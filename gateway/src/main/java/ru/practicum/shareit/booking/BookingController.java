package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ResourceValidationException;
import ru.practicum.shareit.validation.OnCreate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.util.Header;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createBooking(@Valid @RequestBody InputBookingDto inputBookingDto,
                                                @RequestHeader(Header.USER_ID) Long bookerId) {
        log.info("Получен запрос на бронирование вещи={} от пользователя={}", inputBookingDto.getItemId(), bookerId);
        return bookingClient.createBooking(bookerId, inputBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBookingStatus(@RequestHeader(Header.USER_ID) @NotNull Long ownerId,
                                                      @PathVariable @NotNull Long bookingId,
                                                      @RequestParam @NotNull Boolean approved) {
        log.info("Получен запрос на изменение статуса бронирования от пользователя c id={}: {}", ownerId, approved);
        return bookingClient.changeBookingStatus(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(Header.USER_ID) Long userId, @PathVariable Long bookingId) {
        log.info("Получен запрос на просмотр бронирования с id={} от пользователя с id={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader(Header.USER_ID) Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос на просмотр всех бронирований состояния:{} пользователя с id={}", state, userId);
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new ResourceValidationException(String.format("Unknown state: %s", state)));
        return bookingClient.getAllUserBookings(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBookings(@RequestHeader(Header.USER_ID) Long ownerId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос на просмотр всех бронирований состояния:{} владельца с id={}", state, ownerId);
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new ResourceValidationException(String.format("Unknown state: %s", state)));
        return bookingClient.getAllOwnerBookings(ownerId, bookingState, from, size);
    }
}

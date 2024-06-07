package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Header;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Validated(OnCreate.class)
    public BookingDto createBooking(@Valid @RequestBody InputBookingDto inputBookingDto,
                                    @RequestHeader(Header.USER_ID) Long bookerId) {
        log.info("Получен запрос на бронирование вещи={} от пользователя={}", inputBookingDto.getItemId(), bookerId);
        return BookingMapper.toBookingDto(bookingService.addBooking(inputBookingDto, bookerId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeBookingStatus(@RequestHeader(Header.USER_ID) @NotNull Long ownerId,
                                          @PathVariable @NotNull Long bookingId,
                                          @RequestParam @NotNull Boolean approved) {
        log.info("Получен запрос на изменение статуса бронирования от пользователя c id={}: {}", ownerId, approved);
        return BookingMapper.toBookingDto(bookingService.updateBooking(ownerId, bookingId, approved));
    }
}

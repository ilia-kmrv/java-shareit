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
import java.util.Collection;
import java.util.stream.Collectors;

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

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(Header.USER_ID) Long userId, @PathVariable Long bookingId) {
        log.info("Получен запрос на просмотр бронирования с id={} от пользователя с id={}", bookingId, userId);
        return BookingMapper.toBookingDto(bookingService.getBookingByUserId(bookingId, userId));
    }

    @GetMapping
    public Collection<BookingDto> getAllUserBookings(@RequestHeader(Header.USER_ID) Long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос на просмотр всех бронирований состояния:{} пользователя с id={}", state, userId);
        return bookingService.getAllUserBookingsByState(userId, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllOwnerBookings(@RequestHeader(Header.USER_ID) Long ownerId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос на просмотр всех бронирований состояния:{} владельца с id={}", state, ownerId);
        return bookingService.getAllOwnerBookingsByState(ownerId, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}

package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingService {

    Booking addBooking(InputBookingDto inputBookingDto, Long bookerId);

    Booking getBooking(Long id);

    Booking getBookingByUserId(Long bookingId, Long userId);

    Booking updateBooking(Long ownerId, Long bookingId, Boolean approved);

    Collection<Booking> getAllUserBookingsByState(Long userId, String state, Integer from, Integer size);

    Collection<Booking> getAllOwnerBookingsByState(Long ownerId, String state, Integer from, Integer size);

    Collection<Booking> getAllByItemIdAndStatus(Long itemId, BookingStatus status);

    ShortBookingDto getLastBooking(Long itemId);

    ShortBookingDto getNextBooking(Long itemId);

    Collection<Booking> getPastUserBookings(Long itemId, Long userId, LocalDateTime now);
}

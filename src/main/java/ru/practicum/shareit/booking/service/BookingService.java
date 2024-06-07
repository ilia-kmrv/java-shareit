package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {

    Booking addBooking(InputBookingDto inputBookingDto, Long bookerId);

    Booking getBooking(Long id);

    Booking updateBooking(Long ownerId, Long bookingId, Boolean approved);

}

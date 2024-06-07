package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ResourceValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;


    @Override
    @Transactional
    public Booking addBooking(InputBookingDto inputBookingDto, Long bookerId) {
        log.debug("Обработка запроса на бронирование вещи={} от пользователя={}", inputBookingDto.getItemId(), bookerId);
        User user = userService.getUser(bookerId);
        Item item = itemService.getItem(inputBookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new ResourceValidationException("Вещь уже забронирована");
        }

        if (user.getId().equals(item.getOwnerId())) {
            throw new ResourceValidationException("Нельзя бронировать собственную вещь");
        }

        if (inputBookingDto.getEnd().isBefore(inputBookingDto.getStart()) || inputBookingDto.getStart().isEqual(inputBookingDto.getEnd())) {
            throw new ResourceValidationException("Некорректное время бронирования");
        }

        Booking booking = BookingMapper.toBooking(inputBookingDto, item, user, BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Long id) {
        log.debug("Обработка запроса на просмотр бронирования с id={}", id);
        return bookingRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(String.format("Бронирование с id=%d не найдено", id)));
    }

    @Override
    public Booking updateBooking(Long ownerId, Long bookingId, Boolean approved) {
        log.debug("Обработка запроса на изменение статуса бронирования id={} от пользователя={}",
                bookingId, ownerId);
        Booking booking = getBooking(bookingId);
        User owner = userService.getUser(ownerId);

        if (!booking.getItem().getOwnerId().equals(owner.getId())) {
            throw new PermissionDeniedException(
                    String.format("Только владелец=%d вещи=%d может изменять статус бронирования=%d",
                    booking.getItem().getOwnerId(), booking.getItem().getId(), booking.getId()));
        }

        Booking bookingForUpdate = booking.toBuilder()
                .status(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED)
                .build();

        return bookingRepository.save(bookingForUpdate);
    }
}

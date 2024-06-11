package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ResourceValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Util;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public Booking addBooking(InputBookingDto inputBookingDto, Long bookerId) {
        log.debug("Обработка запроса на бронирование вещи={} от пользователя={}", inputBookingDto.getItemId(), bookerId);
        User user = userService.getUser(bookerId);
        Item item = itemRepository.findById(inputBookingDto.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Вещь с id=%d не найдена",
                        inputBookingDto.getItemId())));

        if (!item.getAvailable()) {
            throw new ResourceValidationException("Вещь уже забронирована");
        }

        if (user.getId().equals(item.getOwnerId())) {
            throw new ResourceNotFoundException("Нельзя бронировать собственную вещь");
        }

        if (inputBookingDto.getEnd().isBefore(inputBookingDto.getStart())
                || inputBookingDto.getStart().isEqual(inputBookingDto.getEnd())) {
            throw new ResourceValidationException("Некорректное время бронирования");
        }

        Booking booking = BookingMapper.toBooking(inputBookingDto, item, user, BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBooking(Long id) {
        log.debug("Обработка запроса на просмотр бронирования с id={}", id);
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Бронирование с id=%d не найдено", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingByUserId(Long bookingId, Long userId) {
        log.debug("Обработка запроса на просмотр бронирования с id={}", bookingId);
        Booking booking = getBooking(bookingId);
        User user = userService.getUser(userId);
        if (!booking.getBooker().getId().equals(user.getId()) && !booking.getItem().getOwnerId().equals(user.getId())) {
            throw new ResourceNotFoundException("Просмотр доступен только арендатору или владельцу");
        }
        return booking;
    }

    @Override
    @Transactional
    public Booking updateBooking(Long ownerId, Long bookingId, Boolean approved) {
        log.debug("Обработка запроса на изменение статуса бронирования id={} от пользователя={}",
                bookingId, ownerId);
        Booking booking = getBooking(bookingId);
        User owner = userService.getUser(ownerId);

        if (!booking.getItem().getOwnerId().equals(owner.getId())) {
            throw new ResourceNotFoundException(
                    String.format("Только владелец=%d вещи=%d может изменять статус бронирования=%d",
                            booking.getItem().getOwnerId(), booking.getItem().getId(), booking.getId()));
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ResourceValidationException("Статус бронирования уже был изменён.");
        }

        Booking bookingForUpdate = booking.toBuilder()
                .status(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED)
                .build();

        return bookingRepository.save(bookingForUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> getAllUserBookingsByState(Long userId, String state) {
        log.debug("Обработка запроса на просмотр всех бронирований состояния:{} пользователя с id={}", state, userId);
        userService.getUser(userId);
        List<Booking> bookingsByState;
        BookingState stateValue;

        try {
            stateValue = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ResourceValidationException(String.format("Unknown state: %s", state));
        }

        switch (stateValue) {
            case ALL:
                bookingsByState = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookingsByState = bookingRepository

                        .findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(userId,
                                Util.now(),
                                Util.now());
                break;
            case FUTURE:
                bookingsByState = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId,
                        Util.now());
                break;
            case PAST:
                bookingsByState = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId,
                        Util.now());
                break;
            case WAITING:
                bookingsByState = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingsByState = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED);
                break;
            default:
                return Collections.emptyList();
        }
        return bookingsByState;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> getAllOwnerBookingsByState(Long ownerId, String state) {
        log.debug("Обработка запроса на просмотр всех бронирований состояния:{} владельца с id={}", state, ownerId);
        userService.getUser(ownerId);
        List<Booking> bookingsByState;
        BookingState stateValue;

        try {
            stateValue = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ResourceValidationException(String.format("Unknown state: %s", state));
        }

        switch (stateValue) {
            case ALL:
                bookingsByState = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case CURRENT:
                bookingsByState = bookingRepository
                        .findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(ownerId,
                                Util.now(),
                                Util.now());
                break;
            case FUTURE:
                bookingsByState = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, Util.now());
                break;
            case PAST:
                bookingsByState = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, Util.now());
                break;
            case WAITING:
                bookingsByState = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingsByState = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.REJECTED);
                break;
            default:
                return Collections.emptyList();
        }
        return bookingsByState;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllByItemIdAndStatus(Long itemId, BookingStatus status) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
        return bookingRepository.findByItemIdAndStatus(itemId, status);
    }

    @Override
    public ShortBookingDto getLastBooking(Long itemId) {
        TreeSet<Booking> bookings = getAllByItemIdAndStatus(itemId, BookingStatus.APPROVED).stream()
                .filter(b -> b.getEnd().isBefore(Util.now())
                        || (b.getStart().isBefore(Util.now()) && b.getEnd().isAfter(Util.now())))
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Booking::getEnd))));
        if (bookings.isEmpty()) {
            return null;
        }
        return BookingMapper.toShortBookingDto(bookings.last());
    }

    @Override
    public ShortBookingDto getNextBooking(Long itemId) {
        TreeSet<Booking> bookings = getAllByItemIdAndStatus(itemId, BookingStatus.APPROVED).stream()
                .filter(b -> b.getStart().isAfter(Util.now()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Booking::getStart))));
        if (bookings.isEmpty()) {
            return null;
        }
        return BookingMapper.toShortBookingDto(bookings.first());
    }

    @Override
    public Collection<Booking> getPastUserBookings(Long itemId, Long userId, LocalDateTime now) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
        userService.getUser(userId);
        return bookingRepository.findByItemIdAndBookerIdAndEndBefore(itemId, userId, now);
    }
}


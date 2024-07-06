package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.InputBookingDto;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Test
    void addBooking_whenItemAvailableAndUserIsNotOwnerAndCorrectTime_thenReturnSavedBooking() {
        Long bookerId = 0L;
        LocalDateTime time = LocalDateTime.of(0, 1, 1, 0, 0, 0);
        InputBookingDto dto = InputBookingDto.builder().start(time).end(time.plusMinutes(10)).build();
        User user = User.builder().id(bookerId).build();
        when(userService.getUser(anyLong())).thenReturn(user);
        Item item = Item.builder().available(true).build();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Booking booking = Booking.builder().start(time).end(time.plusMinutes(10)).item(item).booker(user).build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking actualBooking = bookingService.addBooking(dto, bookerId);

        assertEquals(booking, actualBooking);
    }

    @Test
    void addBooking_whenItemNotAvailable_thenValidationExceptionThrown() {
        Long bookerId = 0L;
        LocalDateTime time = LocalDateTime.of(0, 1, 1, 0, 0, 0);
        InputBookingDto dto = InputBookingDto.builder().start(time).end(time.plusMinutes(10)).build();
        User user = User.builder().id(bookerId).build();
        when(userService.getUser(anyLong())).thenReturn(user);
        Item item = Item.builder().available(false).build();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Booking booking = Booking.builder().start(time).end(time.plusMinutes(10)).item(item).booker(user).build();

        assertThrows(ResourceValidationException.class,
                () -> bookingService.addBooking(dto, bookerId));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBooking_whenUserIsOwner_thenNotFoundExceptionThrown() {
        Long bookerId = 0L;
        LocalDateTime time = LocalDateTime.of(0, 1, 1, 0, 0, 0);
        InputBookingDto dto = InputBookingDto.builder().start(time).end(time.plusMinutes(10)).build();
        User user = User.builder().id(bookerId).build();
        when(userService.getUser(anyLong())).thenReturn(user);
        Item item = Item.builder().available(true).ownerId(bookerId).build();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Booking booking = Booking.builder().start(time).end(time.plusMinutes(10)).item(item).booker(user).build();

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.addBooking(dto, bookerId));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBooking_whenIncorrectTime_thenNotFoundExceptionThrown() {
        Long bookerId = 0L;
        LocalDateTime time = LocalDateTime.of(0, 1, 1, 0, 0, 0);
        InputBookingDto dto = InputBookingDto.builder().start(time.plusMinutes(10)).end(time).build();
        User user = User.builder().id(bookerId).build();
        when(userService.getUser(anyLong())).thenReturn(user);
        Item item = Item.builder().available(true).build();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Booking booking = Booking.builder().start(time.plusMinutes(10)).end(time).item(item).booker(user).build();

        assertThrows(ResourceValidationException.class,
                () -> bookingService.addBooking(dto, bookerId));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void updateBooking_whenUserIsOwnerAndStatusIsWaiting_thenReturnUpdatedBooking() {
        Long ownerId = 0L;
        Long bookingId = 0L;
        Boolean approved = true;
        User owner = User.builder().id(ownerId).build();
        Item item = Item.builder().id(0L).ownerId(ownerId).build();
        Booking oldBooking = Booking.builder().item(item).status(BookingStatus.WAITING).build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(oldBooking));
        when(userService.getUser(ownerId)).thenReturn(owner);

        bookingService.updateBooking(ownerId, bookingId, approved);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(BookingStatus.APPROVED, savedBooking.getStatus());
    }

    @Test
    void updateBooking_whenUserIsNotOwner_thenResourceNotFoundExceptionThrown() {
        Long ownerId = 0L;
        Long bookingId = 0L;
        Boolean approved = true;
        User owner = User.builder().build();
        Item item = Item.builder().id(0L).ownerId(ownerId).build();
        Booking oldBooking = Booking.builder().item(item).status(BookingStatus.WAITING).build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(oldBooking));
        when(userService.getUser(ownerId)).thenReturn(owner);

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.updateBooking(ownerId, bookingId, approved));

        verify(bookingRepository, never()).save(oldBooking);
    }

    @Test
    void updateBooking_whenBookingStatusIsNotWaiting_thenResourceValidationExceptionThrown() {
        Long ownerId = 0L;
        Long bookingId = 0L;
        Boolean approved = true;
        User owner = User.builder().id(ownerId).build();
        Item item = Item.builder().id(0L).ownerId(ownerId).build();
        Booking oldBooking = Booking.builder().item(item).status(BookingStatus.REJECTED).build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(oldBooking));
        when(userService.getUser(ownerId)).thenReturn(owner);

        assertThrows(ResourceValidationException.class,
                () -> bookingService.updateBooking(ownerId, bookingId, approved));

        verify(bookingRepository, never()).save(oldBooking);
    }

    @Test
    void getUserBookingByUserId_whenInvoked_thenBookingReturned() {
        Long bookingId = 0L;
        Long userId = 0L;
        User booker = User.builder().id(userId).build();
        Item item = Item.builder().ownerId(11L).build();
        Booking booking = Booking.builder().booker(booker).item(item).build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userService.getUser(userId)).thenReturn(booker);

        Booking bookingByUserId = bookingService.getBookingByUserId(bookingId, userId);

        assertEquals(booking, bookingByUserId);
    }

    @Test
    void getUserBookingByUserId_whenUserNotOwnerOrBooker_thenResourceNotFoundExceptionThrown() {
        Long bookingId = 0L;
        Long userId = 0L;
        User booker = User.builder().id(1L).build();
        Item item = Item.builder().id(1L).ownerId(1L).build();
        Booking booking = Booking.builder().id(bookingId).booker(booker).item(item).build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        User user = User.builder().id(userId).build();
        when(userService.getUser(userId)).thenReturn(user);

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.getBookingByUserId(bookingId, userId));
    }

    @Test
    void getAllUserBookingsByState_whenStateIsIncorrect_thenResourceValidationExceptionThrown() {
        Long userId = 0L;
        String state = "Incorrect";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(userId)).thenReturn(user);

        assertThrows(ResourceValidationException.class,
                () -> bookingService.getAllUserBookingsByState(userId, state, from, size));
    }

    @Test
    void getAllUserBookingsByState_whenStateIsAll_thenAccordingRepositoryMethodInvoked() {
        Long userId = 0L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(userId)).thenReturn(user);
        Pageable page = Util.page(from, size);

        bookingService.getAllUserBookingsByState(userId, state, from, size);

        verify(bookingRepository).findByBookerIdOrderByStartDesc(userId, page);
    }

    @Test
    void getAllUserBookingsByState_whenStateIsCurrent_thenAccordingRepositoryMethodInvoked() {
        Long userId = 0L;
        String state = "CURRENT";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(userId)).thenReturn(user);

        bookingService.getAllUserBookingsByState(userId, state, from, size);

        verify(bookingRepository)
                .findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(anyLong(),
                        any(),
                        any(),
                        any());
    }

    @Test
    void getAllUserBookingsByState_whenStateIsFuture_thenAccordingRepositoryMethodInvoked() {
        Long userId = 0L;
        String state = "FUTURE";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(userId)).thenReturn(user);

        bookingService.getAllUserBookingsByState(userId, state, from, size);

        verify(bookingRepository)
                .findByBookerIdAndStartAfterOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllUserBookingsByState_whenStateIsPast_thenAccordingRepositoryMethodInvoked() {
        Long userId = 0L;
        String state = "PAST";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(userId)).thenReturn(user);

        bookingService.getAllUserBookingsByState(userId, state, from, size);

        verify(bookingRepository)
                .findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllUserBookingsByState_whenStateIsWaiting_thenAccordingRepositoryMethodInvoked() {
        Long userId = 0L;
        String state = "WAITING";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(userId)).thenReturn(user);

        bookingService.getAllUserBookingsByState(userId, state, from, size);

        verify(bookingRepository)
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllUserBookingsByState_whenStateIsRejected_thenAccordingRepositoryMethodInvoked() {
        Long userId = 0L;
        String state = "REJECTED";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(userId)).thenReturn(user);

        bookingService.getAllUserBookingsByState(userId, state, from, size);

        verify(bookingRepository)
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllOwnerBookingsByState_whenStateIsIncorrect_thenResourceValidationExceptionThrown() {
        Long ownerId = 0L;
        String state = "Incorrect";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(ownerId)).thenReturn(user);

        assertThrows(ResourceValidationException.class,
                () -> bookingService.getAllOwnerBookingsByState(ownerId, state, from, size));
    }

    @Test
    void getAllOwnerBookingsByState_whenStateIsAll_thenAccordingRepositoryMethodInvoked() {
        Long ownerId = 0L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(ownerId)).thenReturn(user);
        Pageable page = Util.page(from, size);

        bookingService.getAllOwnerBookingsByState(ownerId, state, from, size);

        verify(bookingRepository).findByItemOwnerIdOrderByStartDesc(ownerId, page);
    }

    @Test
    void getAllOwnerBookingsByState_whenStateIsCurrent_thenAccordingRepositoryMethodInvoked() {
        Long ownerId = 0L;
        String state = "CURRENT";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(ownerId)).thenReturn(user);

        bookingService.getAllOwnerBookingsByState(ownerId, state, from, size);

        verify(bookingRepository)
                .findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(anyLong(),
                        any(),
                        any(),
                        any());
    }

    @Test
    void getAllOwnerBookingsByState_whenStateIsFuture_thenAccordingRepositoryMethodInvoked() {
        Long ownerId = 0L;
        String state = "FUTURE";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(ownerId)).thenReturn(user);

        bookingService.getAllOwnerBookingsByState(ownerId, state, from, size);

        verify(bookingRepository)
                .findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllOwnerBookingsByState_whenStateIsPast_thenAccordingRepositoryMethodInvoked() {
        Long ownerId = 0L;
        String state = "PAST";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(ownerId)).thenReturn(user);

        bookingService.getAllOwnerBookingsByState(ownerId, state, from, size);

        verify(bookingRepository)
                .findByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllOwnerBookingsByState_whenStateIsWaiting_thenAccordingRepositoryMethodInvoked() {
        Long ownerId = 0L;
        String state = "WAITING";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(ownerId)).thenReturn(user);

        bookingService.getAllOwnerBookingsByState(ownerId, state, from, size);

        verify(bookingRepository)
                .findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getAllOwnerBookingsByState_whenStateIsRejected_thenAccordingRepositoryMethodInvoked() {
        Long ownerId = 0L;
        String state = "REJECTED";
        Integer from = 0;
        Integer size = 10;
        User user = new User();
        when(userService.getUser(ownerId)).thenReturn(user);

        bookingService.getAllOwnerBookingsByState(ownerId, state, from, size);

        verify(bookingRepository)
                .findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void getLastBooking_whenInvoked_thenReturnLastBooking() {
        Long itemId = 0L;
        LocalDateTime past = Util.now().minusDays(2);
        LocalDateTime future = Util.now().plusDays(2);
        User booker1 = new User();
        Booking lastBooking = Booking.builder()
                .booker(booker1)
                .start(past.minusHours(1))
                .end(past.plusHours(1))
                .build();
        User booker2 = new User();
        Booking nextBooking = Booking.builder()
                .booker(booker2)
                .start(future.minusHours(1))
                .end(future.plusHours(1))
                .build();
        ShortBookingDto lastBookingDto = BookingMapper.toShortBookingDto(lastBooking);
        List<Booking> bookings = List.of(lastBooking, nextBooking);
        Item item = new Item();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingService.getAllByItemIdAndStatus(anyLong(), BookingStatus.APPROVED)).thenReturn(bookings);

        ShortBookingDto actualBookingDto = bookingService.getLastBooking(itemId);

        assertEquals(lastBookingDto, actualBookingDto);
    }

    @Test
    void getNextBooking_whenInvoked_thenReturnNextBooking() {
        Long itemId = 0L;
        LocalDateTime past = Util.now().minusDays(2);
        LocalDateTime future = Util.now().plusDays(2);
        User booker1 = new User();
        Booking lastBooking = Booking.builder()
                .booker(booker1)
                .start(past.minusHours(1))
                .end(past.plusHours(1))
                .build();
        User booker2 = new User();
        Booking nextBooking = Booking.builder()
                .booker(booker2)
                .start(future.minusHours(1))
                .end(future.plusHours(1))
                .build();
        ShortBookingDto nextBookingDto = BookingMapper.toShortBookingDto(nextBooking);
        List<Booking> bookings = List.of(lastBooking, nextBooking);
        Item item = new Item();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingService.getAllByItemIdAndStatus(anyLong(), BookingStatus.APPROVED)).thenReturn(bookings);

        ShortBookingDto actualBookingDto = bookingService.getNextBooking(itemId);

        assertEquals(nextBookingDto, actualBookingDto);
    }

    @Test
    void getPastUserBookings_whenInvoked_thenRepositoryMethodCalled() {
        Long itemId = 0L;
        Long userId = 0L;
        Item item = new Item();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        User user = new User();
        when(userService.getUser(userId)).thenReturn(user);

        bookingService.getPastUserBookings(itemId, userId);

        verify(bookingRepository).findByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any());
    }
}
package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ResourceValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Util;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Test
    void addItem_whenOwnerFound_thenReturnSavedItemWithOwnerId() {
        Long ownerId = 0L;
        User owner = new User();
        Item item = new Item();
        when(userService.getUser(ownerId)).thenReturn(owner);

        itemService.addItem(item, ownerId);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals(ownerId, savedItem.getOwnerId());
    }

    @Test
    void addItem_whenOwnerNotFound_thenNotFoundExceptionThrown() {
        Long ownerId = 0L;
        Item item = new Item();
        when(userService.getUser(ownerId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () -> itemService.addItem(item, ownerId));

        verify(itemRepository, never()).save(item);
    }

    @Test
    void getItem_whenItemFound_thenReturnItem() {
        Long itemId = 0L;
        Long userId = 0L;
        Item item = Item.builder().ownerId(userId).build();
        User user = new User();
        OwnerItemDto ownerItemDto = ItemMapper.toOwnerItemDto(item, null, null, List.of());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.getUser(userId)).thenReturn(user);

        OwnerItemDto actualItemDto = itemService.getItem(itemId, userId);

        assertEquals(ownerItemDto, actualItemDto);
    }

    @Test
    void getItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        Long itemId = 0L;
        Long userId = 0L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> itemService.getItem(itemId, userId));
    }

    @Test
    void getAllItems_whenInvoked_thenReturnCollectionOfOwnerItemDto() {
        Long ownerId = 0L;
        Integer from = 0;
        Integer size = 10;
        Item item = new Item();
        OwnerItemDto ownerItemDto = ItemMapper.toOwnerItemDto(item, null, null, List.of());
        List<Item> items = List.of(item);
        List<OwnerItemDto> dto = List.of(ownerItemDto);
        Pageable page = Util.page(from, size);
        when(itemRepository.findByOwnerId(ownerId, page)).thenReturn(items);

        Collection<OwnerItemDto> actualDto = itemService.getAllItems(ownerId, from, size);

        assertEquals(dto, actualDto);
    }

    @Test
    void updateItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        Long itemId = 0L;
        Long userId = 0L;
        Item item = new Item();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> itemService.updateItem(item, itemId, userId));

        verify(itemRepository, never()).save(item);
    }

    @Test
    void updateItem_whenOwnerIdDoesNotMatch_thenPermissionDeniedExceptionThrown() {
        Long itemId = 0L;
        Long userId = 0L;
        Long ownerId = 1L;
        Item item = Item.builder().ownerId(ownerId).build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(PermissionDeniedException.class,
                () -> itemService.updateItem(item, itemId, userId));

        verify(itemRepository, never()).save(item);
    }

    @Test
    void updateItem_whenNameNotBlankAndDescriptionNotBlankAndAvailableNotNull_thenReturnItemWithUpdatedFields() {
        Long itemId = 0L;
        Long ownerId = 0L;
        Item oldItem = Item.builder()
                .name("name")
                .description("description")
                .available(false)
                .ownerId(ownerId)
                .build();

        Item newItem = Item.builder()
                .name("name1")
                .description("description1")
                .available(true)
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

        itemService.updateItem(newItem, itemId, ownerId);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals(newItem.getName(), savedItem.getName());
        assertEquals(newItem.getDescription(), savedItem.getDescription());
        assertEquals(newItem.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void updateItem_whenNameIsBlankAndDescriptionIsBlankAndAvailableIsNull_thenReturnItemWithOldFields() {
        Long itemId = 0L;
        Long ownerId = 0L;
        Item oldItem = Item.builder()
                .name("name")
                .description("description")
                .available(false)
                .ownerId(ownerId)
                .build();

        Item newItem = Item.builder()
                .name(" ")
                .description(" ")
                .available(null)
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

        itemService.updateItem(newItem, itemId, ownerId);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals(oldItem.getName(), savedItem.getName());
        assertEquals(oldItem.getDescription(), savedItem.getDescription());
        assertEquals(oldItem.getAvailable(), savedItem.getAvailable());
    }

    @Test
    void deleteItem_whenItemFoundAndOwnerIdVerified_thenRepoDeleteItemInvoked() {
        Long itemId = 0L;
        Long ownerId = 0L;
        Item item = Item.builder().ownerId(ownerId).build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId, ownerId);

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void deleteItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        Long itemId = 0L;
        Long ownerId = 0L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> itemService.deleteItem(itemId, ownerId));

        verify(itemRepository, never()).deleteById(itemId);
    }

    @Test
    void searchItems_whenTextNotBlank_thenReturnCollectionOfItems() {
        String text = "whatever";
        Integer from = 0;
        Integer size = 10;
        Item item = Item.builder().name("whatever name").build();
        List<Item> expectedList = List.of(item);
        Pageable page = Util.page(from, size);
        when(itemRepository.search(text, page)).thenReturn(expectedList);

        Collection<Item> foundItems = itemService.searchItems(text, from, size);

        assertEquals(expectedList, foundItems);
    }

    @Test
    void searchItems_whenTextNotBlankAndInconsistentCase_thenReturnCollectionOfItems() {
        String text = "whAtevEr";
        Integer from = 0;
        Integer size = 10;
        Item item = Item.builder().name("whatever name").build();
        List<Item> expectedList = List.of(item);
        Pageable page = Util.page(from, size);
        when(itemRepository.search(text, page)).thenReturn(expectedList);

        Collection<Item> foundItems = itemService.searchItems(text, from, size);

        assertEquals(expectedList, foundItems);
    }

    @Test
    void searchItems_whenTextIsBlank_thenReturnCollectionOfItems() {
        String text = " ";
        Integer from = 0;
        Integer size = 10;
        List<Item> expectedList = Collections.emptyList();
        Pageable page = Util.page(from, size);

        Collection<Item> foundItems = itemService.searchItems(text, from, size);

        assertEquals(expectedList, foundItems);
        verify(itemRepository, never()).search(text, page);
    }

    @Test
    void addComment_whenItemIsFoundAndUserIsFoundAndUserNotOwnerAndBookingIsFinished_thenReturnCommentDto() {
        Long itemId = 0L;
        Long userId = 0L;
        Long ownerId = 1L;
        Long commentId = 0L;
        LocalDateTime time = LocalDateTime.of(0, 1, 1, 0, 0, 0);
        Item item = Item.builder().ownerId(ownerId).build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        User user = User.builder().id(userId).build();
        when(userService.getUser(userId)).thenReturn(user);
        Comment comment = Comment.builder().id(commentId).item(item).author(user).build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        Booking booking = Booking.builder().end(time).build();
        when(bookingService.getPastUserBookings(anyLong(), anyLong())).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto actualDto = itemService.addComment(commentDto, itemId, userId);

        assertEquals(commentDto, actualDto);
    }

    @Test
    void addComment_whenUserIdEqualsOwnerId_thenValidationExceptionThrown() {
        Long itemId = 0L;
        Long userId = 0L;
        Long ownerId = 0L;
        Long commentId = 0L;
        Item item = Item.builder().ownerId(ownerId).build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        User user = User.builder().id(userId).build();
        when(userService.getUser(userId)).thenReturn(user);
        Comment comment = Comment.builder().id(commentId).item(item).author(user).build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertThrows(ResourceValidationException.class,
                () -> itemService.addComment(commentDto, itemId, userId));

        verify(commentRepository, never()).save(comment);
    }

    @Test
    void addComment_whenBookingNotEndedYet_thenValidationExceptionThrown() {
        Long itemId = 0L;
        Long userId = 0L;
        Long ownerId = 1L;
        Long commentId = 0L;
        Item item = Item.builder().ownerId(ownerId).build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        User user = User.builder().id(userId).build();
        when(userService.getUser(userId)).thenReturn(user);
        Comment comment = Comment.builder().id(commentId).item(item).author(user).build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        when(bookingService.getPastUserBookings(anyLong(), anyLong())).thenReturn(Collections.emptyList());

        assertThrows(ResourceValidationException.class,
                () -> itemService.addComment(commentDto, itemId, userId));

        verify(commentRepository, never()).save(comment);
    }
}
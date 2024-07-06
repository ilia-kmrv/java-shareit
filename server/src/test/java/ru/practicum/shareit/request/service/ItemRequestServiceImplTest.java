package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Util;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Test
    void addItemRequest_whenUserFoundThenReturnSavedRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();
        Long userId = 0L;
        User user = new User();
        ItemRequest expectedRequest = new ItemRequest();
        when(userService.getUser(userId)).thenReturn(user);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(expectedRequest);

        ItemRequest actualRequest = requestService.addItemRequest(itemRequestDto, userId);

        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    void getAllRequests_whenInvoked_thenReturnItemRequestDtoWithItemsCollection() {
        Integer from = 0;
        Integer size = 10;
        Long userId = 0L;
        Pageable page = Util.page(from, size);
        User user = new User();
        Item item = Item.builder().requestId(0L).build();
        ItemDtoForRequest itemDtoForRequest = ItemDtoForRequest.builder().requestId(0L).build();
        ItemRequestDtoWithItems dto1 = ItemRequestDtoWithItems.builder().id(0L).items(List.of(itemDtoForRequest)).build();
        List<ItemRequestDtoWithItems> expectedDtos = List.of(dto1);
        when(userService.getUser(anyLong())).thenReturn(user);
        ItemRequest request1 = ItemRequest.builder().id(0L).build();
        when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, page))
                .thenReturn(List.of(request1));
        when(itemRepository.findAllByRequestIdIn(anySet())).thenReturn(List.of(item));

        List<ItemRequestDtoWithItems> actualDtos = requestService.getAllRequests(from, size, userId);

        assertEquals(expectedDtos, actualDtos);
    }

    @Test
    void getRequestsByUserId_whenInvoked_thenReturnItemRequestDtoWithItemsCollection() {
        Long userId = 0L;
        User user = new User();
        Item item = Item.builder().requestId(0L).build();
        ItemDtoForRequest itemDtoForRequest = ItemDtoForRequest.builder().requestId(0L).build();
        ItemRequestDtoWithItems dto1 = ItemRequestDtoWithItems.builder().id(0L).items(List.of(itemDtoForRequest)).build();
        List<ItemRequestDtoWithItems> expectedDtos = List.of(dto1);
        when(userService.getUser(anyLong())).thenReturn(user);
        ItemRequest request1 = ItemRequest.builder().id(0L).build();
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(request1));
        when(itemRepository.findAllByRequestIdIn(anySet())).thenReturn(List.of(item));

        List<ItemRequestDtoWithItems> actualDtos = requestService.getRequestsByUserId(userId);

        assertEquals(expectedDtos, actualDtos);
    }

    @Test
    void getRequest_whenInvoked_thenReturnItemRequestDtoWithItems() {
        Long requestId = 0L;
        Long userId = 0L;

        User user = new User();
        Item item = new Item();
        ItemRequest request = new ItemRequest();
        ItemDtoForRequest itemDtoForRequest = ItemDtoForRequest.builder().build();
        ItemRequestDtoWithItems expectedDto = ItemRequestDtoWithItems.builder().items(List.of(itemDtoForRequest)).build();
        when(userService.getUser(anyLong())).thenReturn(user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestIdIn(anySet())).thenReturn(List.of(item));

        ItemRequestDtoWithItems actualDto = requestService.getRequest(requestId, userId);

        assertEquals(expectedDto, actualDto);
    }
}
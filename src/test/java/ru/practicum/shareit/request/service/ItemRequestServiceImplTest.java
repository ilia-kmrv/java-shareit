package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
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
    void getAllRequests() {
    }

    @Test
    void getRequestsByUserId() {
    }

    @Test
    void getRequest() {
    }
}
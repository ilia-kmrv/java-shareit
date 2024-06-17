package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDtoWithItems> getRequestsByUserId(Long userId);
}

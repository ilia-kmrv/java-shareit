package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item addItem(Item item, Long ownerId);

    OwnerItemDto getItem(Long itemId, Long userId);

    Collection<OwnerItemDto> getAllItems(Long ownerId, Integer from, Integer size);

    Item updateItem(Item item, Long itemId, Long ownerId);

    void deleteItem(Long itemId, Long ownerId);

    Collection<Item> searchItems(String text, Integer from, Integer size);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}

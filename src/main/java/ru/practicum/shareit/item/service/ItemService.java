package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item addItem(Item item, Long ownerId);

    OwnerItemDto getItem(Long itemId, Long userId);

    Collection<OwnerItemDto> getAllItems(Long ownerId);

    Item updateItem(Item item, Long itemId, Long ownerId);

    void deleteItem(Long itemId, Long ownerId);

    Collection<Item> searchItems(String text);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}

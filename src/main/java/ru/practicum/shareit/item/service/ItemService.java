package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item addItem(Item item, Long ownerId);

    Item getItem(Long itemId);

    Collection<Item> getAllItems(Long ownerId);

    Item updateItem(Item item, Long itemId, Long ownerId);

    void deleteItem(Long itemId, Long ownerId);

    Collection<Item> searchItems(String text);
}

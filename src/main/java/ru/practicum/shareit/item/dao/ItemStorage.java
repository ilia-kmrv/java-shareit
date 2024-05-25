package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.Storage;

import java.util.Collection;

public interface ItemStorage extends Storage<Item> {
    Collection<Item> getAllByOwnerId(Long ownerId);

    Collection<Item> search(String text);
}

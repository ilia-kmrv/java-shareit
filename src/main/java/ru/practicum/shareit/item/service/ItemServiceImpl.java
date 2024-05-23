package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public Item addItem(Item item, Long ownerId) {
        log.debug("Обработка запроса на создание вещи");
        userService.getUser(ownerId);
        Item itemWithOwner = item.toBuilder().ownerId(ownerId).build();
        return itemStorage.create(itemWithOwner);
    }

    @Override
    public Item getItem(Long itemId) {
        log.debug("Обработка запроса на получение вещи");
        return itemStorage.get(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
    }

    @Override
    public Collection<Item> getAllItems(Long ownerId) {
        log.debug("Обработка запроса на получение вещи");
        userService.getUser(ownerId);
        return itemStorage.getAllByOwnerId(ownerId);
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long ownerId) {
        log.debug("Обработка запроса на обновление вещи c id={}", item.getId());
        userService.getUser(ownerId);
        Item itemInDb = getItem(itemId);

        if (!itemInDb.getOwnerId().equals(ownerId)) {
            throw new PermissionDeniedException("Вещь не принадлежит данному пользователю");
        }

        Item itemForUpdate = itemInDb.toBuilder()
                .name(item.getName() == null ? itemInDb.getName() : item.getName())
                .description(item.getDescription() == null ? itemInDb.getDescription() : item.getDescription())
                .available(item.getAvailable() == null ? itemInDb.getAvailable() : item.getAvailable())
                .build();
        return itemStorage.update(itemForUpdate);
    }

    @Override
    public void deleteItem(Long itemId, Long ownerId) {
        log.debug("Обработка запроса на удаление вещи c id={}", itemId);
        userService.getUser(ownerId);
        if (getItem(itemId).getOwnerId().equals(ownerId)) {
            itemStorage.delete(itemId);
        }
    }

    @Override
    public Collection<Item> searchItems(String text) {
        log.debug("Обработка запроса на поиск доступных вещей по тексту text={}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text);
    }
}

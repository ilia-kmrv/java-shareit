package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Util;

import java.util.Collection;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Item addItem(Item item, Long ownerId) {
        log.debug("Обработка запроса на создание вещи");
        userService.getUser(ownerId);
        Item itemWithOwner = item.toBuilder().ownerId(ownerId).build();
        return itemRepository.save(itemWithOwner);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItem(Long itemId) {
        log.debug("Обработка запроса на получение вещи");
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Item> getAllItems(Long ownerId) {
        log.debug("Обработка запроса на получение вещи");
        userService.getUser(ownerId);
        return itemRepository.findByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public Item updateItem(Item item, Long itemId, Long ownerId) {
        log.debug("Обработка запроса на обновление вещи c id={}", item.getId());
        userService.getUser(ownerId);
        Item itemInDb = getItem(itemId);

        if (!itemInDb.getOwnerId().equals(ownerId)) {
            throw new PermissionDeniedException("Вещь не принадлежит данному пользователю");
        }

        Item itemForUpdate = itemInDb.toBuilder()
                .name(Util.isBlank(item.getName()) ? itemInDb.getName() : item.getName())
                .description(Util.isBlank(item.getDescription()) ? itemInDb.getDescription() : item.getDescription())
                .available(item.getAvailable() == null ? itemInDb.getAvailable() : item.getAvailable())
                .build();
        return itemRepository.save(itemForUpdate);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long ownerId) {
        log.debug("Обработка запроса на удаление вещи c id={}", itemId);
        userService.getUser(ownerId);
        if (getItem(itemId).getOwnerId().equals(ownerId)) {
            itemRepository.deleteById(itemId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Item> searchItems(String text) {
        log.debug("Обработка запроса на поиск доступных вещей по тексту text={}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text);
    }
}

package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long idCount = 0L;

    private Long generateId() {
        return ++idCount;
    }

    @Override
    public Item create(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        log.debug("Вещь {} c id={} успешно добавлена", item.getName(), item.getId());
        return item;
    }

    @Override
    public Optional<Item> get(Long itemId) {
        log.debug("Получение вещи id={} из памяти", itemId);
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Collection<Item> getAll() {
        log.debug("Получение всех вещей из памяти");
        return items.values().stream().collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getAllByOwnerId(Long ownerId) {
        log.debug("Получение всех вещей пользователя из памяти");
        return items.values().stream().filter(i -> i.getOwnerId().equals(ownerId)).collect(Collectors.toList());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        log.debug("Пользователь {} с id={} успешно обновлён", item.getName(), item.getId());
        return item;
    }

    @Override
    public void delete(Long id) {
        log.debug("Удаление вещи с id={} из памяти", id);
        if (get(id).isPresent()) {
            items.remove(id);
        } else {
            throw new ResourceNotFoundException(
                    String.format("Удаление невозможно, вещи с id=%d нет в памяти.", id));
        }
    }

    @Override
    public Collection<Item> search(String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(searchText) ||
                        i.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }

}

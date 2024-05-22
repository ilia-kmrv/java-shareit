package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCount = 0L;

    private Long generateId() {
        return ++idCount;
    }

    @Override
    public User create(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("Пользователь {} c id={} успешно добавлен", user.getName(), user.getId());
        return user;
    }

    @Override
    public Optional<User> get(Long userId) {
        log.debug("Получение пользователя id={} из памяти", userId);
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Collection<User> getAll() {
        log.debug("Получение всех пользователей из памяти");
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.debug("Пользователь {} с id={} успешно обновлён", user.getName(), user.getId());
        return user;
    }

    @Override
    public void delete(Long id) {
        log.debug("Удаление пользователя с id={} из памяти", id);
        if (get(id).isPresent()) {
            users.remove(id);
        } else {
            throw new ResourceNotFoundException(
                    String.format("Удаление невозможно, пользователя с id=%d нет в памяти.", id));
        }
    }
}

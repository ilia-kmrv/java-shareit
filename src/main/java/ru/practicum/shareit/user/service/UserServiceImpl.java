package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceAlreadyExistsException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User addUser(User user) {
        log.debug("Обработка запроса на создание пользователя");
        checkEmail(user);
        return userStorage.create(user);
    }

    @Override
    public User getUser(Long id) {
        log.debug("Получение пользователя по id={}", id);
        return userStorage.get(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    @Override
    public Collection<User> getAllUsers() {
        log.debug("Обработка запроса на получение списка пользователей");
        return userStorage.getAll().stream().collect(Collectors.toList());
    }

    @Override
    public User updateUser(User user, Long id) {
        log.debug("Обработка запроса на обновление пользователя c id={}", user.getId());
        User userInDb = getUser(id);
        User userForUpdate = userInDb.toBuilder()
                .name((user.getName() == null || user.getName().isBlank()) ? userInDb.getName() : user.getName())
                .email(user.getEmail() == null  ? userInDb.getEmail() : user.getEmail())
                .build();
        checkEmail(userForUpdate);
        return userStorage.update(userForUpdate);
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Обработка запроса на удаление пользователя с id={}", id);
        getUser(id);
        userStorage.delete(id);
    }

    private void checkEmail(User user) {
        boolean isNotUniqueEmail = userStorage.getAll().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId()));

        if (isNotUniqueEmail) {
            throw new ResourceAlreadyExistsException(String.format("Указанный Email %s уже занят", user.getEmail()));
        }
    }
}

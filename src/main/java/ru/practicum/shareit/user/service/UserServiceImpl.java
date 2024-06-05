package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Util;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User addUser(User user) {
        log.debug("Обработка запроса на создание пользователя");
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        log.debug("Получение пользователя по id={}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> getAllUsers() {
        log.debug("Обработка запроса на получение списка пользователей");
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(User user, Long id) {
        log.debug("Обработка запроса на обновление пользователя c id={}", user.getId());
        User userInDb = getUser(id);
        User userForUpdate = userInDb.toBuilder()
                .name(Util.isBlank(user.getName()) ? userInDb.getName() : user.getName())
                .email(user.getEmail() == null ? userInDb.getEmail() : user.getEmail())
                .build();
        return userRepository.save(userForUpdate);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Обработка запроса на удаление пользователя с id={}", id);
        getUser(id);
        userRepository.deleteById(id);
    }

}

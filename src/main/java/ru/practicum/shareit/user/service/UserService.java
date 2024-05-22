package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    // CRUD
    User addUser(User user);

    User getUser(Long userId);

    Collection<User> getAllUsers();

    User updateUser(User user, Long userId);

    void deleteUser(Long userId);
}

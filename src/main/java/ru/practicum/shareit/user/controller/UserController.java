package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    @Validated(OnCreate.class)
    public UserDto postUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.addUser(user));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Получен запрос на просмотр пользователя c id={}", userId);
        return UserMapper.toUserDto(userService.getUser(userId));
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("Получен запрос на просмотр всех пользователей");
        return userService.getAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @PatchMapping("/{userId}")
    @Validated(OnUpdate.class)
    public UserDto patchUser(@Valid @RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Получен запрос на обновление пользователя c id={}", userId);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.updateUser(user, userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя c id={}", userId);
        userService.deleteUser(userId);
    }

}

package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void addUser_whenInvoked_thenReturnSavedUser() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User actualUser = userService.addUser(user);

        assertEquals(user, actualUser);
        verify(userRepository).save(user);
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() {
        Long userId = 0L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User actualUser = userService.getUser(userId);

        assertEquals(user, actualUser);
    }

    @Test
    void getUser_whenUserNotFound_thenResourceNotFoundExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUser(userId));
    }

    @Test
    void getAllUsers_whenInvoked_thenReturnCollectionOfUsers() {
        List<User> users = List.of(new User());
        when(userRepository.findAll()).thenReturn(users);

        Collection<User> actualUsers = userService.getAllUsers();

        assertEquals(users, actualUsers);
    }

    @Test
    void updateUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(user, userId));
    }

    @Test
    void updateUser_whenNameNotBlankAndEmailNotNull_ThenReturnUserWithUpdatedFields() {
        Long userId = 0L;
        User oldUser = User.builder()
                .name("name")
                .email("email@mail.com")
                .build();

        User newUser = User.builder()
                .name("new name")
                .email("newemail@mail.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.updateUser(newUser, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(newUser.getName(), savedUser.getName());
        assertEquals(newUser.getEmail(), savedUser.getEmail());
    }

    @Test
    void updateUser_whenNameIsBlankAndEmailIsNull_ThenReturnUserWithOldFields() {
        Long userId = 0L;
        User oldUser = User.builder()
                .name("name")
                .email("email@mail.com")
                .build();

        User newUser = User.builder()
                .name(" ")
                .email(null)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        userService.updateUser(newUser, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(oldUser.getName(), savedUser.getName());
        assertEquals(oldUser.getEmail(), savedUser.getEmail());
    }

    @Test
    void deleteUser_whenUserFound_thenDeleteUserInvoked() {
        Long userId = 0L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(userId));
    }
}
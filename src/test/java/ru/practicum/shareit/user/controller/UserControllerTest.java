package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    @MockBean
    private UserService userService;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(0L)
                .name("username")
                .email("username@email.com")
                .build();
    }


    @SneakyThrows
    @Test
    void postUser_whenInvoked_thenStatusIsOkAndUserDtoReturned() {
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
        when(userService.addUser(any())).thenReturn(user);

        String result = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(user), result);
    }

    @SneakyThrows
    @Test
    void postUser_whenInvokedWrongEmail_thenStatusIsBadRequestAndErrorPrompted() {
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email("mail.mail.com")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(userService, never()).addUser(user);
    }

    @SneakyThrows
    @Test
    void postUser_whenInvokedWithBlankName_thenStatusIsBadRequestAndErrorPrompted() {
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .name(" ")
                .email(user.getEmail())
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(userService, never()).getUser(any());
    }

    @SneakyThrows
    @Test
    void postUser_whenInvokeWithNullEmail_thenStatusIsBadRequestAndErrorPrompted() {
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(null)
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(userService, never()).getUser(any());
    }

    @SneakyThrows
    @Test
    void getUser_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        Long userId = 0L;
        when(userService.getUser(anyLong())).thenReturn(user);

        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).getUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        when(userService.getAllUsers()).thenReturn(List.of());

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();

        verify(userService).getAllUsers();
    }

    @SneakyThrows
    @Test
    void patchUser_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        Long userId = 0L;
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
        when(userService.updateUser(any(), anyLong())).thenReturn(user);

        String result = mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(user), result);
    }

    @SneakyThrows
    @Test
    void patchUser_whenUserDtoIsNotValid_thenStatusIsBadRequest() {
        Long userId = 0L;
        User userForUpdate = user.toBuilder().email("wrong-email.com").build();

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userForUpdate)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(userForUpdate, userId);
    }

    @SneakyThrows
    @Test
    void deleteUser_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        Long userId = 0L;

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}
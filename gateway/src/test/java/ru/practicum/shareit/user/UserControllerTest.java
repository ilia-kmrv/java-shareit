package ru.practicum.shareit.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

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
    private final UserClient userClient;

    UserDto dto;

    @BeforeEach
    void setUp() {
        dto = UserDto.builder()
                .id(0L)
                .name("name")
                .email("email@email.com")
                .build();
    }


    @SneakyThrows
    @Test
    void postUser_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        when(userClient.postUser(any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userClient).postUser(dto);
    }

    @SneakyThrows
    @Test
    void postUser_whenInvokedWrongEmail_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        dto.setEmail("wrong.email.com");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(userClient, never()).postUser(dto);
    }

    @SneakyThrows
    @Test
    void postUser_whenInvokedWithBlankName_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        dto.setName(" ");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(userClient, never()).postUser(dto);
    }

    @SneakyThrows
    @Test
    void postUser_whenInvokeWithNullEmail_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        dto.setEmail(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(userClient, never()).postUser(dto);
    }

    @SneakyThrows
    @Test
    void getUser_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long userId = 0L;
        when(userClient.getUser(anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).getUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        when(userClient.getAllUsers()).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();

        verify(userClient).getAllUsers();
    }

    @SneakyThrows
    @Test
    void patchUser_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long userId = 0L;
        when(userClient.patchUser(any(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        String result = mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userClient).patchUser(dto, userId);
    }

    @SneakyThrows
    @Test
    void patchUser_whenUserDtoIsNotValid_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Long userId = 0L;
        dto.setEmail("wrong.email.com");

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).patchUser(dto, userId);
    }

    @SneakyThrows
    @Test
    void deleteUser_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long userId = 0L;
        when(userClient.deleteUser(userId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(userId);
    }
}

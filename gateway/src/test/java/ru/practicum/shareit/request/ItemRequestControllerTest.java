package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.Header;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    @MockBean
    private final RequestClient requestClient;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {

        itemRequestDto = ItemRequestDto.builder()
                .id(0L)
                .description("description")
                .build();
    }

    @SneakyThrows
    @Test
    void postItemRequest_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long userId = 0L;
        when(requestClient.postItemRequest(itemRequestDto, userId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(post("/requests")
                        .header(Header.USER_ID, userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestClient).postItemRequest(itemRequestDto, userId);
    }

    @SneakyThrows
    @Test
    void postItemRequest_whenDescriptionIsBlank_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Long userId = 0L;
        itemRequestDto.setDescription(" ");

        mvc.perform(post("/requests")
                        .header(Header.USER_ID, userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).postItemRequest(itemRequestDto, userId);
    }

    @SneakyThrows
    @Test
    void getRequestsByUser_whenInvoked_thenStatusIsOkClientMethodCalled() {
        Long userId = 0L;
        when(requestClient.getRequestsByUser(userId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/requests")
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk());

        verify(requestClient).getRequestsByUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Integer from = 0;
        Integer size = 10;
        Long userId = 0L;
        when(requestClient.getAllRequests(from, size, userId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/requests/all")
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(requestClient).getAllRequests(from, size, userId);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenFromIsNegative_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Integer from = -1;
        Integer size = 10;
        Long userId = 0L;

        mvc.perform(get("/requests/all")
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).getAllRequests(from, size, userId);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenSizeIsNegative_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Integer from = 1;
        Integer size = -10;
        Long userId = 0L;

        mvc.perform(get("/requests/all")
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).getAllRequests(from, size, userId);
    }

    @SneakyThrows
    @Test
    void getRequest_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long requestId = 0L;
        Long userId = 0L;
        when(requestClient.getRequest(requestId, userId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk());

        verify(requestClient).getRequest(requestId, userId);

    }
}
